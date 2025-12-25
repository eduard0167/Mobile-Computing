from typing import Optional
from pydantic import BaseModel, EmailStr
from models import Role

class RegisterDto(BaseModel):
    email: EmailStr
    password: str
    confirmPassword: str
    firstName: str
    lastName: str
    phoneNumber: str
    faculty: str = None
    year: str = None
    role: Optional[Role] = Role.STUDENT

class LoginDto(BaseModel):
    email: EmailStr
    password: str

class RefreshDto(BaseModel):
    refreshToken: str

class Tokens(BaseModel):
    accessToken: str
    refreshToken: str
