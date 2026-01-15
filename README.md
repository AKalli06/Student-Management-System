# Student-Management-System
A menu‑driven client–server system built in Java, designed with communication protocols and has robust error handling. This project demonstrates how to build a predictable, stable, and scalable socket‑based system where the client and server exchange structured message blocks using a custom text protocol.

🚀 Key Features

🔗 Client–Server Architecture

• 	Built using Java sockets (TCP).

• 	Deterministic request–response protocol with:

• 	Prompt blocks
• 	Response blocks
• 	Menu blocks
• 	Blank‑line terminators for synchronization

📊 Student Analytics

The server provides multiple analytics operations:

• 	Overall statistics
• 	Filter students by year
• 	Search by name substring
• 	Sort students (ascending, descending, alphabetical)
• 	Export failing students
• 	Add new students to the database

🧠 Protocol‑Safe Communication

• 	Every server response ends with a blank line for clean parsing.
• 	Every menu is sent exactly once per cycle.
• 	The client reads:
• 	Prompt → Response → Menu

🛡️ Robust Error Handling

• 	Handles invalid menu choices
• 	Detects malformed input (non‑numeric year, invalid sort order, etc.)
• 	Gracefully recovers using consistent response blocks
• 	Prevents desynchronization between client and server

📁 File‑Backed Storage

• 	Student records are stored in a text file.
• 	Adding a student updates the file and reloads the in‑memory array.
• 	Exporting failing students writes a filtered file.

🧩 Technical Highlights

• 	Clean separation of concerns:
• 	 → user interaction + protocol reading
• 	 → request handling + menu logic
• 	 → analytics and file operations
• 	Uses  for efficient string construction.
• 	Uses structured message blocks to avoid partial reads.
• 	Designed for clarity, maintainability, and extensibility.
