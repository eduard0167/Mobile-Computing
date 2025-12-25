from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from database import engine
import models
from users import router as users_router
from auth import router as auth_router
from buildings import router as buildings_router
from rooms import router as rooms_router
from reservations import router as reservations_router

# Create database tables
models.Base.metadata.create_all(bind=engine)

app = FastAPI()

origins = [
    "http://localhost:5173",  # or wherever your React app runs
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],  # or explicitly ["POST", "GET", "OPTIONS"]
    allow_headers=["*"],
)

# Include routers
app.include_router(users_router.router)
app.include_router(auth_router.router)
app.include_router(buildings_router.router)
app.include_router(rooms_router.router)
app.include_router(reservations_router.router)

app.mount("/images", StaticFiles(directory="images"))

