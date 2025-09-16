
# Music App

A simple music library management and storage app. Contains a RESTful API for uploading, organizing, and retrieving MP3 files.

#### Features
- Upload, manage, and retrieve MP3 song files
- Create playlists and add songs to them



## Getting Started

#### Prerequisites

- Java 21
- Docker

#### Installation

1. Clone the repo

```sh
git clone https://github.com/ddecker4/musicapp.git
cd musicapp
```
2. Build server application
```sh
./buildapp
```
3. Ensure Docker service is running
4. Start Docker container
```sh
docker compose up
```
## Usage

#### POST `/api/songs`

- Add a new song to the library.

- Request Body:
```json
{
    "file" : "File (audio/mp3)"
}
```
- Response Body:
```json
{
    "id" : 0,
    "title" : "filename",
    "artist" : null
}
```

#### GET `/api/songs`

- Retrieve all songs in the library.

- Response Body:
```json
[
    {
        "id" : 0,
        "title" : "song name",
        "artist" : "artist name"
    }
]
```

#### GET `/api/songs/{song_id}`

- Retrieve song metadata by ID.

- Response Body:
```json
{
    "id" : 123,
    "title" : "song name",
    "artist" : "artist name"
}
```

#### PUT `/api/songs/{song_id}`

- Update song metadata by ID.

- Request Body:
```json
{
    "title" : "new song name",
    "artist" : "new artist name"
}
```

- Response Body:
```json
{
    "id" : 0,
    "title" : "new song name",
    "artist" : "new artist name"
}
```

#### DELETE `/api/songs/{song_id}`

- Delete song from library by ID.

#### POST `/api/playlists`

- Create a new playlist.

- Request Body:
```json
{
    "title" : "playlist title"
}
```

- Response Body:
```json
{
    "id" : 0,
    "title" : "playlist title"
}
```

#### GET `/api/playlists`

- Retrieve all playlists.

- Response Body:
```json
[
    {
        "id" : 0,
        "title" : "playlist title"
    }
]
```

#### GET `/api/playlists/{playlist_id}`

- Retrieve playlist metadata by ID.

- Response Body:
```json
{
    "id" : 123,
    "title" : "playlist title"
}
```

#### PUT `/api/playlists/{playlist_id}`

- Update playlist metadata by ID.

- Request Body:
```json
{
    "title" : "new playlist title"
}
```

- Response Body:
```json
{
    "id" : 0,
    "title" : "new playlist title"
}
```

#### DELETE `/api/playlists/{playlist_id}`

- Delete playlist by ID.

#### POST `/api/playlists/{playlist_id}/entries`

- Add a song to playlist.

- Request Body:
```json
{
    "song_id" : 123
}
```

- Response Body:
```json
{
    "id" : 0,
    "position" : 0,
    "playlist_id" : 123,
    "song_id" : 123
}
```

#### GET `/api/playlists/{playlist_id}/entries`

- Retrieve all entries in a playlist.

- Response Body:
```json
[
    {
        "id" : 0,
        "position" : 0,
        "playlist_id" : 123,
        "song_id" : 123
    }
]
```

#### GET `/api/playlists/entries/{entry_id}`

- Retrieve playlist entry by ID.

- Response Body:
```json
{
    "id" : 0,
    "position" : 0,
    "playlist_id" : 123,
    "song_id" : 123 
}
```

#### PUT `/api/playlists/entries/{entry_id}`

- Update the position of a song in a playlist.

- Request Body:
```json
{
    "position" : 2
}
```

- Response Body:
```json
{
    "id" : 0,
    "position" : 2,
    "playlist_id" : 123,
    "song_id" : 123
}
```

#### DELETE `/api/playlists/entries/{entry_id}`

- Remove song from playlist by ID.
## Tech Stack

- Java 21 - Core language
- Spring Boot - Framework for server
- Spring Web - Spring module for creating RESTful API
- Spring Data JPA - Spring module for connecting Spring Boot to PostgresQL
- PostgresQL - Relational database for managing music library
- MinIO - Object storage for MP3 files
- Docker - Containerization

