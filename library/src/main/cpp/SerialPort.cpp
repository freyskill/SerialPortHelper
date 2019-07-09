//
// Created by frey on 2018/6/4.
//

#include <fcntl.h>
#include <termios.h>
#include <unistd.h>

#include "include/SerialPort.h"

SerialPort::SerialPort() {}

SerialPort::SerialPort(const char *path) {
    SerialPort::path = path;
}

speed_t SerialPort::getBaudrate(int baudrate) {
    switch (baudrate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return -1;
    }
}

int SerialPort::setSpeed(int fd, int speed) {
    speed_t b_speed;
    struct termios cfg;
    b_speed = getBaudrate(speed);
    if (tcgetattr(fd, &cfg)) {
        LOGE("tcgetattr invocation method failed!");
        close(fd);
        return FALSE;
    }

    cfmakeraw(&cfg);
    cfsetispeed(&cfg, b_speed);
    cfsetospeed(&cfg, b_speed);

    if (tcsetattr(fd, TCSANOW, &cfg)) {
        LOGE("tcsetattr invocation method failed!");
        close(fd);
        return FALSE;
    }
    return TRUE;
}

int SerialPort::setParity(int fd, int databits, int stopbits, char parity) {
    struct termios options;
    if (tcgetattr(fd, &options) != 0) {
        LOGE("The method tcgetattr exception!");
        return FALSE;
    }
    options.c_cflag &= ~CSIZE;
    switch (databits)                                           /* Set data bits */
    {
        case 7:
            options.c_cflag |= CS7;
            break;
        case 8:
            options.c_cflag |= CS8;
            break;
        default:
            LOGE("Unsupported data size!");
            return FALSE;
    }
    switch (parity) {
        case 'n':
        case 'N':
            options.c_cflag &= ~PARENB;                         /* Clear parity enable */
            options.c_iflag &= ~INPCK;                          /* Enable parity checking */
            break;
        case 'o':
        case 'O':
            options.c_cflag |= (PARODD | PARENB);               /* Set odd checking */
            options.c_iflag |= INPCK;                           /* Disnable parity checking */
            break;
        case 'e':
        case 'E':
            options.c_cflag |= PARENB;                          /* Enable parity */
            options.c_cflag &= ~PARODD;                         /* Transformation even checking */
            options.c_iflag |= INPCK;                           /* Disnable parity checking */
            break;
        case 'S':
        case 's':  /*as no parity*/
            options.c_cflag &= ~PARENB;
            options.c_cflag &= ~CSTOPB;
            break;
        default:
            LOGE("Unsupported parity!");
            return FALSE;
    }
    /* 设置停止位*/
    switch (stopbits) {
        case 1:
            options.c_cflag &= ~CSTOPB;
            break;
        case 2:
            options.c_cflag |= CSTOPB;
            break;
        default:
            LOGE("Unsupported stop bits!");
            return FALSE;
    }
    /* Set input parity option */
    if (parity != 'n')
        options.c_iflag |= INPCK;
    tcflush(fd, TCIFLUSH);
    options.c_cc[VTIME] = 150;                                  /* Set timeout to 15 seconds */
    options.c_cc[VMIN] = 0;                                     /* Update the options and do it NOW */
    if (tcsetattr(fd, TCSANOW, &options) != 0) {
        LOGE("The method tcsetattr exception!");
        return FALSE;
    }
    return TRUE;
}

int SerialPort::openSerialPort(SerialPortConfig config) {
    LOGD("Open device!");
    isClose = false;
    fd = open(path, O_RDWR);
    if (fd < 0) {
        LOGE("Error to read %s port file!", path);
        return FALSE;
    }

    if (!setSpeed(fd, config.baudrate)) {
        LOGE("Set Speed Error!");
        return FALSE;
    }
    if (!setParity(fd, config.databits, config.stopbits, config.parity)) {
        LOGE("Set Parity Error!");
        return FALSE;
    }
    LOGD("Open Success!");
    return TRUE;
}

int SerialPort::readData(BYTE *data, int size) {

    int ret, retval;
    fd_set rfds;
    ret = 0;

    if (isClose) return 0;
    for (int i = 0; i < size; i++) {
        data[i] = static_cast<char>(0xFF);
    }
    FD_ZERO(&rfds);     //清空集合
    FD_SET(fd, &rfds);  //把要检测的句柄fd加入到集合里
    // TODO Async operation. Thread blocking.
    if (FD_ISSET(fd, &rfds)) {
        FD_ZERO(&rfds);
        FD_SET(fd, &rfds);
        retval = select(fd + 1, &rfds, NULL, NULL, NULL);
        if (retval == -1) {
            LOGE("Select error!");
        } else if (retval) {
            LOGD("This device has data!");
            ret = static_cast<int>(read(fd, data, static_cast<size_t>(size)));
        } else {
            LOGE("Select timeout!");
        }
    }
    if (isClose) close(fd);
    return ret;
}

int SerialPort::writeData(BYTE *data, int len) {
    int result;
    result = static_cast<int>(write(fd, data, static_cast<size_t>(len)));
    return TRUE;
}

int SerialPort::setMode(int mode){
   struct termios options;
   if(tcgetattr(fd,&options) != 0){
       LOGE("The method tcgetattr exception!");
       return FALSE;
   }
   if(mode != 0){
       if(mode ==1){
           options.c_lflag &=~(ICANON | ECHO | ECHOE | ISIG);   //input
           options.c_oflag &=~OPOST;                            //out put
       }else if(mode == 2){
           options.c_lflag |=(ICANON | ECHO | ECHOE | ISIG);    //input
           options.c_oflag |=OPOST;                             //out put
       }
       if(tcsetattr(fd,TCSANOW,&options) != 0){
           LOGE("The method tcsetattr exception!");
           return FALSE;
       }
   }
   return TRUE;
}

void SerialPort::closePort() {
    LOGD("Close device!");
    isClose = true;
}