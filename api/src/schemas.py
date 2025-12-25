from pydantic import BaseModel, EmailStr
from typing import Optional, List
from datetime import datetime
from enum import Enum


# Enums
class Role(str, Enum):
    STUDENT = "STUDENT"
    PROFESSOR = "PROFESSOR"
    ADMIN = "ADMIN"
    SECURITY = "SECURITY"

class StatusRoom(str, Enum):
    BUSY = "BUSY"
    WAITING = "WAITING"
    AVAILABLE = "AVAILABLE"

class Status(str, Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
    ONGOING = "ONGOING"
    FINISHED = "FINISHED"
    CANCELED = "CANCELED"
    NOT_ATTENDED = "NOT_ATTENDED"


# Base Schemas
class UserBase(BaseModel):
    email: EmailStr
    firstName: str
    lastName: str
    phoneNumber: str
    faculty: Optional[str]
    year: Optional[str]
    role: Role = Role.STUDENT

class UserCreate(UserBase):
    password: str

class User(UserBase):
    id: int
    createdAt: datetime
    updatedAt: datetime

    class Config:
        from_attributes = True


class BuildingBase(BaseModel):
    name: str
    university: str
    description: Optional[str]
    imageUrls: List[str]
    latitude: float
    longitude: float
    address: str

class Building(BuildingBase):
    id: int
    createdAt: datetime
    updatedAt: datetime

    class Config:
        from_attributes = True


class RoomBase(BaseModel):
    name: str
    description: Optional[str]
    capacity: int = 1
    characteristics: List[str]
    imageUrls: List[str]
    buildingId: int
    status: StatusRoom = StatusRoom.AVAILABLE

class Room(RoomBase):
    id: int
    createdAt: datetime
    updatedAt: datetime

    class Config:
        from_attributes = True


class ReservationBase(BaseModel):
    userId: int
    roomId: int
    event: str
    startTime: datetime
    endTime: datetime
    status: Status = Status.PENDING
    observations: Optional[str]

class Reservation(ReservationBase):
    id: int
    createdAt: datetime
    updatedAt: datetime

    class Config:
        from_attributes = True
