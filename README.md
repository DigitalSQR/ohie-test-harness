# Testing-harness-tool
Testing harness tool is a complete test framework that will facilitate testing how well technologies align to the OpenHIE Architecture specification and health and data content, as specified by WHO SMART Guidelines.

## [Docker Configuration](#docker-configuration)
1. [Docker Installed](#docker-installation)
2. [Docker Setup](#docker-setup)

### [Docker Installation](#docker-installation)
- One can refer the official Docker website at https://docs.docker.com/engine/install/ubuntu/ or visit DigitalOcean's documentation for instructions on installing Docker on Ubuntu 20.04. The steps outlined are sourced from DigitalOcean's guide available at https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-20-04.
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