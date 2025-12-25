from fastapi import APIRouter, Depends, HTTPException, status, Form, UploadFile, File
from sqlalchemy.orm import Session
from typing import List, Optional
from database import get_db 
from auth.utils import validate_admin_role, get_current_user_id
from utils.FileUploadService import FileUploadService
from sqlalchemy.orm import joinedload
from models import Building, User, Room

router = APIRouter(prefix="/rooms", tags=["Rooms"])
fileUploader = FileUploadService()

@router.post("/")
async def create_room(
    name: str = Form(...),
    buildingId: int = Form(...),
    capacity: int = Form(1),
    characteristics: str = Form(""),
    images: List[UploadFile] = File([]),
    db: Session = Depends(get_db)
):
    print("Creating room with the following data:")

    building = db.query(Building).filter(Building.id == buildingId).first()
    image_urls = await fileUploader.save_files(f"{building.name}/{name}", name, images)
    new_room_data = {
        "name": name,
        "buildingId": buildingId,
        "capacity": capacity,
        "characteristics": characteristics,
        "imageUrls": image_urls,
    }
    new_room = Room(**new_room_data)
    db.add(new_room)
    db.commit()
    db.refresh(new_room)
    return new_room

@router.get("/")
def find_all(user_id: int = Depends(get_current_user_id), db: Session = Depends(get_db)):
    return db.query(Room).options(joinedload(Room.building)).all()


@router.get("/{id}")
def find_one(id: int, user_id: int = Depends(get_current_user_id), db: Session = Depends(get_db)):
    room = db.query(Room).options(joinedload(Room.building)).filter(Room.buildingId == id).all()
    if not room:
        raise HTTPException(status_code=404, detail="Room not found")
    return room


@router.get("/{buildingName}/{roomName}")
def find_room_from_building(buildingName: str, roomName: str, user_id: int = Depends(get_current_user_id), db: Session = Depends(get_db)):
    room = db.query(Room).options(joinedload(Room.building)).filter(Room.name == roomName).filter(Building.name == buildingName).first()
    if not room:
        raise HTTPException(status_code=404, detail="Room not found")
    return room


@router.delete("/{roomId}/force")
async def force_remove(roomId: int, user_id: int = Depends(validate_admin_role),  db: Session = Depends(get_db)):
    room = db.query(Room).options(joinedload(Room.building)).filter(Room.buildingId == roomId).first()
    await fileUploader.delete_files(f"{room.building.name}/{room.name}")
    db.delete(room)
    db.commit()
