# RescueGrid — Flood SOS & Emergency Response System (Java OOP + Live Web UI)

**RescueGrid** is an Object-Oriented Programming (OOP) emergency response and flood triage system built in Java alongside a live interactive web command center.

---

## Key Features

1. **Live GPS Map of Verified Incidents**:
   - Interactive coordinate map of Pune & Pimpri-Chinchwad (`18.45°N–18.65°N, 73.75°E–73.94°E`) with rotating radar sweep, sonar ripple animations, and urgency-coded pins.
2. **Citizen Mobile SOS App & Offline 2G SMS Distress Gateway**:
   - Citizens can submit GPS-tagged Flood or Medical distress calls via the 1-tap Mobile App UI or send structured offline SMS strings (`SOS|FLOOD|Kothrud|18.5074,73.8077|6|5|9876543210`).
3. **Location-Based Volunteer Proximity Alerts**:
   - Computes real-time Haversine GPS distances (`calculateDistanceKm`) between incidents and volunteers, broadcasting instant alerts to available volunteers within the selected radius.
4. **Safe Shelters & Relief Camp Capacity Tracker**:
   - Tracks bed occupancy and admits rescued citizens (`Shelter.admit()`).

---

## Flood SOS Color Palette

| Element / Role | Color Name | Hex Code | Text Color on Top |
| :--- | :--- | :--- | :--- |
| **Primary (Header, Main Buttons)** | Deep Navy | `#0B3C5D` | White (`#FFFFFF`) |
| **Secondary (Highlights, Water Feel)** | Flood Blue | `#328CC1` | White (`#FFFFFF`) |
| **Background** | Soft Ice White | `#F4F8FB` | Charcoal (`#1B2733`) |
| **SOS Button & Critical Urgency** | Alert Red | `#D62828` | White (`#FFFFFF`) |
| **High Urgency** | High Orange | `#F77F00` | White (`#FFFFFF`) |
| **Medium Urgency / Warnings** | Amber | `#FCBF49` / `#F4A100` | Charcoal (`#1B2733`) |
| **Low Urgency** | Flood Blue | `#328CC1` | White (`#FFFFFF`) |
| **Safe Shelter & Resolved Status** | Safe Green | `#2E9E5B` | White (`#FFFFFF`) |

---

## Project Structure

```text
RescueGrid/
├── index.html                           # Standalone Interactive Web UI (deploys directly on GitHub Pages / Vercel)
├── README.md                            # Project documentation & deployment guide
├── .gitignore                           # Git ignore rules for Java & IDEs
├── data/
│   └── requests.txt                     # Persisted emergency requests (CSV with GPS coordinates)
└── src/
    └── com/
        └── rescuegrid/
            ├── Urgent.java              # Interface for urgency calculation & color coding
            ├── Request.java             # Abstract base class with GPS coordinates & verification
            ├── FloodRequest.java        # Subclass for flood emergencies (waterLevel * 8)
            ├── MedicalRequest.java      # Subclass for medical emergencies (+40 critical / +10 standard)
            ├── Person.java              # Base class for people with GPS location
            ├── Citizen.java             # Subclass for citizens composing SOS SMS requests
            ├── Volunteer.java           # Subclass for volunteers with skills & alert inbox
            ├── Shelter.java             # Relief shelter capacity & admission manager
            ├── SmsDistressGateway.java  # Parses Citizen SMS & Mobile App distress payloads
            ├── RescueService.java       # Triage sorting, Haversine GPS distance & nearby volunteer alerts
            ├── FileStorage.java         # Reads/writes requests to data/requests.txt
            ├── RescueGridGUI.java       # Java Swing desktop UI with Flood SOS palette
            ├── WebServer.java           # Built-in Java HTTP server for hosting index.html on port 8080
            └── Main.java                # Console menu entry point
```

---

## How to Push to GitHub & Deploy Live (GitHub Pages / Vercel / Netlify)

### Step 1: Push All Files to Your GitHub Repository
Unzip `RescueGrid_GitHub_Deploy_Ready.zip` into your local `RescueGrid` folder and run:

```bash
git init
git remote add origin https://github.com/aaryasuryavanshi01-lab/RescueGrid.git
git branch -M main
git add .
git commit -m "Add Flood SOS interactive UI, Live GPS Map, Citizen SMS/App SOS, and nearby volunteer alerts"
git push -u origin main
```

### Step 2: Deploy the Interactive UI on GitHub Pages (Free & Instant)
1. Open your repository on GitHub: `https://github.com/aaryasuryavanshi01-lab/RescueGrid`
2. Click **Settings** (top tab) -> **Pages** (left sidebar).
3. Under **Build and deployment** -> **Branch**, select **`main`** and folder **`/ (root)`**, then click **Save**.
4. In ~60 seconds, your live website will be published at:
   `https://aaryasuryavanshi01-lab.github.io/RescueGrid/`

*(Alternatively, you can import the GitHub repo into **Vercel** or **Netlify** with zero configuration—both automatically detect `index.html` in the root directory.)*

---

## How to Compile & Run the Java OOP Code Locally

```bash
# Compile all Java files
mkdir -p bin
javac -d bin src/com/rescuegrid/*.java

# Run the interactive Console Menu
java -cp bin com.rescuegrid.Main

# Or run the automated verification demo
java -cp bin com.rescuegrid.Main --demo
```
