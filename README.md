# Kalman Mobile

A tool to implement and understand [Kalman Filters](https://en.wikipedia.org/wiki/Kalman_filter).

Kalman Mobile along with [Kalman Desktop](https://github.com/imaginelenses/kalmanDesk) is a Server-Client pair that reads the accelerometer readings from an Android device and sends them over to a desktop application over websockets.
Kalman Desktop then moves the cursor on the screen according to the change in acceleration of the mobile device.

## Usage
1. Download and install [Kalman Mobile](https://github.com/imaginelenses/kalmanMobile/releases/latest/download/kalman.apk)
2. Provide camera access manually (TODO handle permissions)
3. Download and run [Kalman Desktop](https://github.com/imaginelenses/kalmanDesk)
4. Scan the QR code shown fom Kalman Mobile
5. Turn on or off the _Move_ switch to move the cursor

## Accelerometer Readings
A JSON containing acceleration along `x`, `y` and `z` in m/s<sup>2</sup> and a [`timestamp`](https://developer.android.com/reference/android/hardware/SensorEvent#timestamp) in nanoseconds (time since the device has been on) is sent to Kalman Desktop.
```java
    private final DecimalFormat f = new DecimalFormat("#0.000000");
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        JSONObject data = new JSONObject();
        try {
            data.put("x", f.format(event.values[0]));
            data.put("y", f.format(event.values[1]));
            data.put("z", f.format(event.values[2]));
            data.put("timestamp", event.timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        socket.emit("move_event", data);
    }
```

### Note
Kalman Mobile reads [`Sensor.TYPE_ACCELEROMETER`](https://developer.android.com/reference/android/hardware/SensorEvent#sensor.type_accelerometer:) and **not** [`Sensor.TYPE_ACCELEROMETER_UNCALIBRATED`](https://developer.android.com/reference/android/hardware/SensorEvent#sensor.type_accelerometer_uncalibrated:). Meaning it is bias compensated. 


## Websocket Compatibility
| Server  | Client |
| ------- | ------ |
| [Netty-socketio](https://github.com/mrniko/netty-socketio) v1.7.19  | [Socket.IO-client Java](https://github.com/socketio/socket.io-client-java) v1.0.1 |

## References for Kalman filter
* [Kalmanfilter.net](https://www.kalmanfilter.net/)
* [Kalman's seminal paper (1960)](http://www.cs.unc.edu/~welch/kalman/media/pdf/Kalman1960.pdf)
* [UNC's collection of resources](https://www.cs.unc.edu/~welch/kalman/index.html)

## License
GNU General Public License

#
_App icon stolen from kalmanfilter.net_
