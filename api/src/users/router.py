from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List
from pydantic import BaseModel, EmailStr
from datetime import datetime

from database import get_db  # Make sure this imports from your main DB setup
from models import User, Role  # Assuming your models are in models.py

router = APIRouter(prefix="/users", tags=["Users"])

# Pydantic Schemas
class UserOut(BaseModel):
    id: int
    email: EmailStr
    firstName: str
    lastName: str
    phoneNumber: str
    faculty: str
    year: str
    role: Role
    createdAt: datetime
    updatedAt: datetime

    class Config:
        from_attributes = True

class UserUpdate(BaseModel):
    firstName: str = None
    lastName: str = None
    phoneNumber: str = None
    faculty: str = None
    year: str = None
    role: Role = None

# Routes

@router.get("/", response_model=List[UserOut])
def read_users(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return db.query(User).offset(skip).limit(limit).all()

@router.get("/{user_id}", response_model=UserOut)
def read_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

@router.put("/{user_id}", response_model=UserOut)
def update_user(user_id: int, updates: UserUpdate, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    for key, value in updates.dict(exclude_unset=True).items():
        setattr(user, key, value)
    
    db.commit()
    db.refresh(user)
    return user

@router.delete("/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    db.delete(user)
    db.commit()
    return None
