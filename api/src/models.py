from sqlalchemy import Column, Integer, String, DateTime, Float, Enum, ForeignKey, Table, ARRAY
from sqlalchemy.orm import declarative_base, relationship
from datetime import datetime
import enum

Base = declarative_base()

# Enums
class Role(str, enum.Enum):
    STUDENT = "STUDENT"
    PROFESSOR = "PROFESSOR"
    ADMIN = "ADMIN"
    SECURITY = "SECURITY"

class StatusRoom(str, enum.Enum):
    BUSY = "BUSY"
    WAITING = "WAITING"
    AVAILABLE = "AVAILABLE"

class Status(str, enum.Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
    ONGOING = "ONGOING"
    FINISHED = "FINISHED"
    CANCELED = "CANCELED"
    NOT_ATTENDED = "NOT_ATTENDED"

# Models
class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    createdAt = Column(DateTime, default=datetime.utcnow)
    updatedAt = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    email = Column(String, unique=True, nullable=False)
    password = Column(String, nullable=False)
    firstName = Column(String, nullable=False)
    lastName = Column(String, nullable=False)
    phoneNumber = Column(String, unique=True, nullable=False)
    faculty = Column(String)
    year = Column(String)
    role = Column(Enum(Role), default=Role.STUDENT)
    refreshToken = Column(String)

    reservations = relationship("Reservation", back_populates="user")


class Building(Base):
    __tablename__ = "buildings"
    id = Column(Integer, primary_key=True, index=True)
    createdAt = Column(DateTime, default=datetime.utcnow)
    updatedAt = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    name = Column(String, nullable=False)
    university = Column(String, nullable=False)
    description = Column(String)
    imageUrls = Column(ARRAY(String))  # Serialize as JSON string or separate table
    latitude = Column(Float)
    longitude = Column(Float)
    address = Column(String)

    rooms = relationship("Room", back_populates="building")


class Room(Base):
    __tablename__ = "rooms"
    id = Column(Integer, primary_key=True, index=True)
    createdAt = Column(DateTime, default=datetime.utcnow)
    updatedAt = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    name = Column(String, nullable=False)
    description = Column(String)
    capacity = Column(Integer, default=1)
    characteristics = Column(String)  # Same note as imageUrls
    imageUrls = Column(ARRAY(String))
    buildingId = Column(Integer, ForeignKey("buildings.id", ondelete="CASCADE"))
    status = Column(Enum(StatusRoom), default=StatusRoom.AVAILABLE)

    building = relationship("Building", back_populates="rooms")
    reservations = relationship("Reservation", back_populates="room")


class Reservation(Base):
    __tablename__ = "reservations"
    id = Column(Integer, primary_key=True, index=True)
    createdAt = Column(DateTime, default=datetime.utcnow)
    updatedAt = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    userId = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"))
    roomId = Column(Integer, ForeignKey("rooms.id", ondelete="CASCADE"))
    event = Column(String)
    startTime = Column(DateTime)
    endTime = Column(DateTime)
    status = Column(Enum(Status), default=Status.PENDING)
    observations = Column(String)

    user = relationship("User", back_populates="reservations")
    room = relationship("Room", back_populates="reservations")
