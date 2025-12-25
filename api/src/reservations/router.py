from fastapi import APIRouter, Depends, HTTPException, status, Form, UploadFile, File
from sqlalchemy.orm import Session
from pydantic import BaseModel
from auth.utils import get_current_user_id
from database import get_db 
from datetime import datetime
from sqlalchemy.orm import joinedload
from models import Reservation, Room
from schemas import Status
from typing import Optional

router = APIRouter(prefix="/reservations", tags=["Reservations"])

class ReservationRequest(BaseModel):
    roomId: int
    event: str
    startTime: datetime
    endTime: datetime

class ReservationUpdate(BaseModel):
    status: Optional[Status] = None
    observations: Optional[str] = None


@router.post("")
def create_reservation(
    body: ReservationRequest,
    user_id: int = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    reservations = db.query(Reservation).filter(
        Reservation.roomId == body.roomId,
        Reservation.startTime < body.endTime,
        Reservation.endTime > body.startTime
    ).first()

    if reservations:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Room is already reserved for the selected time."
        )

    new_reservation = Reservation(**body.model_dump())
    new_reservation.userId = user_id
    db.add(new_reservation)
    db.commit()
    db.refresh(new_reservation)
    return new_reservation


@router.get("/room/{roomId}")
def get_reservations_for_room(
    roomId: int,
    user_id: int = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    return db.query(Reservation).options(joinedload(Reservation.user), joinedload(Reservation.room).joinedload(Room.building)).filter(Reservation.roomId == roomId).all()


@router.get("/user/{userId}")
def get_reservations_for_user(
    userId: int,
    user_id: int = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    return db.query(Reservation).options(joinedload(Reservation.user), joinedload(Reservation.room).joinedload(Room.building)).filter(Reservation.userId == userId).all()


@router.get("")
def get_reservations(
    user_id: int = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    return db.query(Reservation).options(joinedload(Reservation.user), joinedload(Reservation.room).joinedload(Room.building)).all()

@router.patch("/{reservation_id}")
def update_reservation(
    reservation_id: int,
    data: ReservationUpdate,
    user_id: int = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    reservation = db.query(Reservation).filter(Reservation.id == reservation_id).first()

    if not reservation:
        raise HTTPException(status_code=404, detail="Reservation not found")

    if data.status is not None:            
        reservation.status = data.status

    if data.observations is not None:
        reservation.observations = data.observations
    
    db.commit()
    db.refresh(reservation)
    return reservation

@router.delete("/{reservation_id}")
def delete_reservation(
    reservation_id: int,
    user_id: int = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    reservation = db.query(Reservation).filter(Reservation.id == reservation_id).first()

    if not reservation:
        raise HTTPException(status_code=404, detail="Reservation not found")

    db.delete(reservation)
    db.commit()
    return {"message": "Reservation deleted successfully"}