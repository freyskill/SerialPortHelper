//
// Created by frey on 2018/6/4.
//

#ifndef SERIALPORTHELPER_SERIALPORT_H
#define SERIALPORTHELPER_SERIALPORT_H
#include <sys/ioctl.h>

#include "SerialPortLog.h"

typedef unsigned char BYTE;

/** define states. */
#define FALSE  0
#define TRUE   1

/** Serial port config param. */
struct SerialPortConfig {
    int baudrate;   // read speed
    int databits;   // one of 7,8
    int stopbits;   // one of 1,2
    char parity;    // one of N,E,O,S
};

/** Serial port device class. */
class SerialPort {
private:
    const char *path;   // device path
    int fd;             // device
    bool isClose;       // is close fd

    /**
     * @brief  Set serial port speed (baudrate).
     * @param  fd     type int device file
     * @param  speed  type int serial port baurate
     * @return  is success
     */
    int setSpeed(int fd, int speed);

    /**
     * @brief   Set data bits,stop bits and parity.
     * @param  fd       type int device file
     * @param  databits type int data bits,one of 7,8
     * @param  stopbits type int stop bits,one of 1,2
     * @param  parity   type char parity,one of N,E,O,S
    */
    int setParity(int fd, int databits, int stopbits, char parity);

public:
    SerialPort();

    /**
     * @brief Construction method.
     * @param path serial port device path
     */
    SerialPort(const char *path);

    /**
     * @brief Open serial port device.
     * @param config serial port device config param
     * @return is success
     */
    int openSerialPort(SerialPortConfig config);

    /**
     * @brief Read device data.
     * @param data read data
     * @param timeval read time
     * @return data length
     */
    int readData(BYTE *data,int size);

    /**
     * @brief Write serial port data.
     * @param data serial port data
     * @return is success
     */
    int writeData(BYTE *data, int len);

    /**
     * raw mode or not
     */
    int setMode(int mode);

    /** @brief Close serial port device. */
    void closePort();

    /**
     * @brief Get baudrate by int.
     * @param baudrate int
     * @return baudrate
     */
    speed_t getBaudrate(int baudrate);
};
#endif //SERIALPORTHELPER_SERIALPORT_H
