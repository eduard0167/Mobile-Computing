from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    JWT_SECRET: str
    JWT_REFRESH_SECRET: str
    DATABASE_URL: str

    class Config:
        env_file = ".env"

settings = Settings()
