from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from datetime import timedelta, datetime
from jose import jwt, JWTError
from passlib.hash import bcrypt

from database import get_db
from models import User, Role
from auth.schemas import RegisterDto, LoginDto, RefreshDto, Tokens
from auth.utils import get_current_user_id
from config import settings

router = APIRouter(prefix="/auth", tags=["Auth"])

def hash_password(password: str) -> str:
    return bcrypt.hash(password)

def verify_password(plain: str, hashed: str) -> bool:
    return bcrypt.verify(plain, hashed)

def create_tokens(user: User) -> Tokens:
    base_payload = {
        "userId": user.id,
        "email": user.email,
        "role": user.role,
    }

    now = datetime.utcnow()

    access_payload = {
        **base_payload,
        "exp": now + timedelta(days=1)
    }

    refresh_payload = {
        **base_payload,
        "exp": now + timedelta(days=2)
    }

    access_token = jwt.encode(
        access_payload,
        settings.JWT_SECRET,
        algorithm="HS256"
    )

    refresh_token = jwt.encode(
        refresh_payload,
        settings.JWT_REFRESH_SECRET,
        algorithm="HS256"
    )

    return Tokens(accessToken=access_token, refreshToken=refresh_token)


def update_refresh_token(user_id: int, token: str, db: Session):
    hashed = hash_password(token)
    db.query(User).filter(User.id == user_id).update({"refreshToken": hashed})
    db.commit()


@router.post("/signup", response_model=Tokens, status_code=status.HTTP_201_CREATED)
def signup(dto: RegisterDto, db: Session = Depends(get_db)):
    if dto.password != dto.confirmPassword:
        raise HTTPException(status_code=400, detail="Passwords do not match")

    if db.query(User).filter(User.email == dto.email).first():
        raise HTTPException(status_code=409, detail="Email already exists")

    if db.query(User).filter(User.phoneNumber == dto.phoneNumber).first():
        raise HTTPException(status_code=409, detail="Phone number already exists")

    hashed_password = hash_password(dto.password)

    user = User(
        email=dto.email,
        password=hashed_password,
        firstName=dto.firstName,
        lastName=dto.lastName,
        phoneNumber=dto.phoneNumber,
        faculty=dto.faculty,
        year=dto.year,
        role=dto.role
    )

    db.add(user)
    db.commit()
    db.refresh(user)

    tokens = create_tokens(user)
    update_refresh_token(user.id, tokens.refreshToken, db)

    return tokens


@router.post("/signin", response_model=Tokens)
def signin(dto: LoginDto, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == dto.email).first()
    if not user or not verify_password(dto.password, user.password):
        raise HTTPException(status_code=400, detail="Invalid credentials")

    tokens = create_tokens(user)
    update_refresh_token(user.id, tokens.refreshToken, db)

    return tokens


@router.post("/logout")
def logout(user_id: int = Depends(get_current_user_id), db: Session = Depends(get_db)):
    db.query(User).filter(User.id == user_id).update({"refreshToken": None})
    db.commit()
    return {"message": "Logged out"}


@router.post("/refresh", response_model=Tokens)
def refresh(dto: RefreshDto, db: Session = Depends(get_db)):
    try:
        payload = jwt.decode(dto.refreshToken, settings.JWT_REFRESH_SECRET, algorithms=["HS256"])
        email = payload.get("email")
    except JWTError:
        raise HTTPException(status_code=403, detail="Access denied")

    user = db.query(User).filter(User.email == email).first()
    if not user or not user.refreshToken or not verify_password(dto.refreshToken, user.refreshToken):
        raise HTTPException(status_code=403, detail="Access denied")

    tokens = create_tokens(user)
    update_refresh_token(user.id, tokens.refreshToken, db)

    return tokens


@router.get("/me")
def get_me(user_id: int = Depends(get_current_user_id), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    return {
        "id": user.id,
        "email": user.email,
        "firstName": user.firstName,
        "lastName": user.lastName,
        "phoneNumber": user.phoneNumber,
        "faculty": user.faculty,
        "year": user.year,
        "role": user.role,
    }
