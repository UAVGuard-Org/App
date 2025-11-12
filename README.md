## UAVGuard
---

### About

Tool for deauthenticating and controlling drones.

- Scan WiFi Devices: View access points (APs) in real-time.
- Send Deauth Packets: Disconnect the operator from the selected AP.
- Take Control: Send packets using the control interface.

### How it works

A deauthentication attack exploits a fundamental but insecure feature of the Wi-Fi protocol (IEEE 802.11) to perform a denial-of-service (DoS) attack against connected users.

This attack relies on sending deauthentication packets. While these packets are a necessary part of the protocol for normal connection management, they are neither encrypted nor authenticated.

This means an attacker can forge and inject these packets into the network, using only the MAC addresses of the router and the target. Upon receiving the fraudulent packet, the device is tricked into believing that the router requested the interruption, forcing it to disconnect.

Since the Unmanned Aerial Vehicle (UAV) is configured to accept only one control connection at a time, the attacker takes advantage of the legitimate operator's disconnection to quickly seize control. The attacker then sends malicious control packets that were built based on traffic data previously decrypted and analyzed with a sniffer, allowing control of the aircraft based on the attacker's inputs.

### Currently supported models

| Model | Link | Last Plugin Version |
| ------------- | ------------- |
| INOVA WRJ-12620 | https://inovaoficial.com.br/produtos/drone-edicao-2-4ghz-aeronave-desmontavel-wrj-12620-inova/ | 1.0.0 |
| IIGENAI E88 Pro | https://iigenaieletronicos.com.br/produtos/mini-drone-e88-pro-com-camera-dupla-wifi-2-baterias/ | 1.0.0 |

### Build & Run

```
mvn clean install & mvn javafx:run -pl app
```
