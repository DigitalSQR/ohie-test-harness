# Testing-harness-tool
Testing harness tool is a complete test framework that will facilitate testing how well technologies align to the OpenHIE Architecture specification and health and data content, as specified by WHO SMART Guidelines.

## Index
- [Prerequisite](#prerequisite)
- [Project Setup](#project-setup)
- [Docker Configuration](#docker-configuration)

## [Prerequisite](#prerequisites)
1. [Git Installed](#git-installation)
2. [Node Installed](#node-installation)
3. [JDK Installed](#jdk-installation)
4. [Postgress Installed](#postgress-installation)
5. [Maven Installed](#maven-installation)

## [Git Installation](#git-installation)

### Windows
1. Visit the [official Git website](https://git-scm.com/).
2. Download the Windows installer.
3. Run the installer and follow the installation wizard instructions.
4. Once installed, open Command Prompt or Git Bash and verify Git installation by running:
   ```
   git --version
   ```
### macOS
1. Git is pre-installed on macOS. To check if Git is already installed, open a terminal window and run:
    ```
    git --version
    ```
2. If Git is not installed, you can install it using Homebrew. Install Homebrew if you haven't already by running:
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
3. Once Homebrew is installed, use it to install git:
    ```
    brew install git
    ```
### Linux (Ubuntu)
1. Open a terminal window.
2. Update the package index and install Git using apt:
    ```
    sudo apt update
    sudo apt install git
    ```
3. Verify Git installation by running:
    ```
    git --version
    ```
## [Node Installation](#node-installation)
### Windows
1. Visit the [official Node.js website](https://nodejs.org/).
2. Download the Windows installer for Node.js version 18.18.0.
3. Run the installer and follow the installation wizard instructions.
4. Once installed, open a command prompt and verify the Node.js version by running:
   ```bash
   node -v
   ```
### macOS
1. Open a terminal window.
2. Install Homebrew if you haven't already. Run the following command:
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
3. Once Homebrew is installed, use it to install Node.js:
    ```
    brew install node@18
    ```
4. Verify Node.js and npm installation by running:
    ```
    node -v
    npm -v
    ```
### Linux (Ubuntu)
1. Open a terminal window.
2. Update the package index and install Node.js using apt:
    ```
    sudo apt update
    curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
    sudo apt-get install -y nodejs
    ```
3. Verify Node.js and npm installation by running:
    ```
    node -v
    npm -v
    ```
## [JDK Installation](#jdk-installation)
### Windows
1. Visit the [official Oracle JDK website](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html).
2. Download the Windows installer (.msi) for JDK 17.
3. Run the installer and follow the installation wizard instructions.
4. Once installed, set the `JAVA_HOME` environment variable:
   - Right-click on 'This PC' or 'My Computer' and select 'Properties'.
   - Click on 'Advanced system settings'.
   - Click on 'Environment Variables'.
   - Under 'System variables', click 'New' and add a variable named `JAVA_HOME` with the JDK installation directory as its value (e.g., `C:\Program Files\Java\jdk-17`).
   - Click 'OK' to save changes.
### macOS
1. Open a terminal window.
2. Install Homebrew if you haven't already. Run the following command:
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
3. Once Homebrew is installed, use it to install AdoptOpenJDK 17:
    ```
    brew install --cask adoptopenjdk/openjdk/adoptopenjdk17
    ```
4. Verify the installation by running:
    ```
    java -version
    ```
### Linux (Ubuntu)
1. Open a terminal window.
2. Update the package index and install OpenJDK 17 using apt:
    ```
    sudo apt update
    sudo apt install openjdk-17-jdk
    ```
3. Verify the installation by running:
    ```
    java -version
    ```
## [Postgress Installation](#postgress-installation)
### Windows
1. Visit the official PostgreSQL website.
2. Download the installer for your Windows version (either 32-bit or 64-bit).
3. Run the installer and follow the installation wizard instructions.
4. During installation, you'll be prompted to set a password for the default postgres user. Remember this password as it will be needed later.
5. Complete the installation process.
6. Optionally, you may install pgAdmin, a graphical administration tool for PostgreSQL, which is often bundled with the PostgreSQL installer.
### macOS
1. Open a terminal window.
2. Install Homebrew if you haven't already. Run the following command:
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
3. Once Homebrew is installed, use it to install PostgreSQL:
    ```
    brew install postgresql
    ```
4. Start the PostgreSQL service:
    ```
    brew services start postgresql
    ```
### Linux (Ubuntu)
1. Open a terminal window.
2. Update the package index and install PostgreSQL:
    ```
    sudo apt update
    sudo apt install postgresql
    ```
3. PostgreSQL will automatically create a default user called postgres. You may need to set a password for this user using:
    ```
    sudo passwd postgres
    ```
4. Start the PostgreSQL service:
    ```
    sudo service postgresql start
    ```
## [Maven Installation](#maven-installation)
### Step 1: Download Maven
Visit the [official Apache Maven website](https://maven.apache.org/download.cgi) and download the latest stable release of Maven.
### Step 2: Extract the Archive
Once the download is complete, extract the downloaded archive file to a location on your system. For example, on Unix-based systems, you can use the following command to extract the archive:
```
tar -zxvf apache-maven-<version>.tar.gz
```
Replace <version> with the version number of Maven you downloaded.
### Step 3: Set up Environment Variables
#### Windows
1. Open Control Panel and go to System and Security > System > Advanced system settings.
2. Click on the "Environment Variables" button.
3. Under "System Variables", click "New" and add the following variables:
    - Variable name: M2_HOME
    - Variable value: Path to the directory where Maven is extracted (e.g., C:\apache-maven-<version>).
4. Find the "Path" variable under "System Variables", click "Edit", and add the following entry:
    %M2_HOME%\bin
5. Click "OK" to save the changes.
#### macOS/Linux
1. Open a terminal window.
2. Edit the .bash_profile or .bashrc file in your home directory and add the following lines:
    ```
    export M2_HOME=/path/to/apache-maven-<version>
    export PATH=$PATH:$M2_HOME/bin
    ```
    Replace /path/to/apache-maven-<version> with the actual path where Maven is extracted.
3. Save the changes and exit the text editor.
### Step 4: Verify Installation
To verify that Maven is installed correctly, open a new terminal or command prompt window and run the following command:
    ```
    mvn -v
    ```
## [Project Setup](#project-setup)
1. [Clone git repository](#clone-git-repository)
2. [Setting Up Logging Service](#logging-service)
3. [Database configuration](#database-configuration)

### [Clone git repository](#clone-git-repository)
If you want to access the repository anonymously or if you don't have SSH keys set up on your GitLab account, use HTTPS. Run the following command in your terminal:
    ```
    git clone https://argusgit.argusoft.com/path/testing-harness-tool.git
    ```

### [Setting Up Logging Service And Required Directory for the Automation Test Script](#logging-service)
#### Step 1: Create Directories
1. Open a terminal window.
2. Navigate to the `/srv` directory:
   ```
   cd /srv
   ```
3. Create a directory named tht:
    ```
    mkdir tht
    ```
4. Inside the tht directory, create two more directories:
    -   error-logs to store error logs based on dates.
        ```
        mkdir tht/error-logs
        ```
    -   files where DocumentService related files will be stored.
        ```
        mkdir tht/files
        ```
    -   files where Automation Test Script related files will be stored.
        ```
        mkdir tht/test
        ```
#### Step 2: Set Permissions
Run this command for permission update:
    ```
    sudo chmod -R a+rwx /srv/tht
    ```

### [Database configuration](#database-configuration)
The Testing Harness Tool uses a PostgreSQL database to store testing data. The name of the database used by the application is `testing_harness_tool`.
You can create the database using the following SQL command:
```sql
CREATE DATABASE testing_harness_tool;
```

## [Docker Configuration](#docker-configuration)
1. [Docker Installed](#docker-installation)
2. [Docker Setup](#docker-setup)

### [Docker Installation](#docker-installation)
#### Linux(Ubuntu)
1. Update existing list of packages:
    ```
    sudo apt update
    ```
2. Install a few prerequisite packages:
    ```
    sudo apt install apt-transport-https ca-certificates curl software-properties-common
    ```
    These let apt use packages over HTTPS.
3. Install curl:
    ```
    sudo apt update
    ```
    This command updated existing list of packages.
    ```
    sudo apt install curl
    ```
    This command will install the curl package.
    ```
    curl --version
    ```
    This command will give the version of curl that has been installed. You can use this to check curl installation result.
    ```
    curl http://www.example.com/
    ```
    This command fetches the contents of the given webpage. It is used to check of curl is working properly.
4. Add GPG key: 
    ```
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    ```
    This adds the GPG key of official docker repository in your system.
5. Add Docker repository to APT sources:
    ```
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
    ```
    This will also update our package database with the Docker packages from the newly added repo.
6. Run this command in the terminal:
    ```
    apt-cache policy docker-ce
    ```
    This command makes sure you are about to install from the Docker repo instead of the default Ubuntu repo
    You’ll see output like this, although the version number for Docker may be different:
    ```
    docker-ce:
        Installed: (none)
        Candidate: 5:19.03.9~3-0~ubuntu-focal
        Version table:
            5:19.03.9~3-0~ubuntu-focal 500
                500 https://download.docker.com/linux/ubuntu focal/stable amd64 Packages
    ```
    Notice that docker-ce is not installed, but the candidate for installation is from the Docker repository for Ubuntu 20.04 (focal).
7. Install Docker
    ```
    sudo apt install docker-ce
    ```
    Docker should now be installed, the daemon started, and the process enabled to start on boot.
8. Check if it is running:
    ```
    sudo systemctl status docker
    ```
    The output should be similar to the following, showing that the service is active and running:
    ```
    ● docker.service - Docker Application Container Engine
        Loaded: loaded (/lib/systemd/system/docker.service; enabled; vendor preset: enabled)
        Active: active (running) since Tue 2020-05-19 17:00:41 UTC; 17s ago
    TriggeredBy: ● docker.socket
        Docs: https://docs.docker.com
    Main PID: 24321 (dockerd)
        Tasks: 8
        Memory: 46.4M
        CGroup: /system.slice/docker.service
                └─24321 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
    ```
### [Docker Setup](#docker-setup)

#### Step 1: Directory Modifications and Initial Zip File Movement
1. Go to the project directory.
2. Execute the following command:
    ```
    sudo bash ./copy_zips.sh files
    ```
3. The terminal will display the list of files copied to the directory `/srv/tht/files`.
4. Grant permissions to that folder using the following command:
    ```
    sudo chmod -R 777 /srv/tht
    ```

#### Step 2: Launch Docker-Compose
1. Navigate to the project directory.
2. Run the following command:
    ```
    sudo docker compose up
    ```
3. The `.env` file is available to configure ports and other settings. If needed, make changes there.
4. Attempt to access `http://localhost:8080/` to reach the testing-harness-tool's login page.