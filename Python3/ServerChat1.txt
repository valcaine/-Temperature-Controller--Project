import socket
from threading import Thread
import serial
import numpy as np

def SendCMDtoRS232(ComPort,command):
    cmdtext = str(command)+'\r\n'
    cmd = bytes(cmdtext, 'utf-8')
    ComPort.write(cmd)
    try:
        result = ComPort.readline()  #get 64 bytes from tiva c and plot them
    except:
        pass    
    return result

def DriveCommand(conn, ip, port, ComPort, MAX_BUFFER_SIZE = 1024):
    rawdata = conn.recv(MAX_BUFFER_SIZE) 
    command = rawdata.decode("utf8").rstrip()
    print(command)
    cmdtext = str(command)+'\r\n'
    cmd = bytes(cmdtext, 'utf-8')
    ComPort.write(cmd)
    try:
        result = ComPort.readline()  #get 64 bytes from tiva c and plot them
    except:
        pass    
    
    #result = str(result)
    #senddata = result.encode("utf8")  # encode the result string
    #conn.sendall(senddata)  # send it to client    
    conn.send(result)  # send it to client
    conn.close()  # close connection

def ConnectToDevice():   
    ComPort = serial.Serial(
    port='COM16',
    baudrate=115200,
    parity=serial.PARITY_NONE,
    stopbits=serial.STOPBITS_ONE,
    bytesize=serial.EIGHTBITS,
    timeout=1
    )
    
    if(ComPort.isOpen()):
        print('Connected to Device.')
    else:
        print('Connection Fault')
    
    return ComPort
    
def StartTCP():
    IP = socket.gethostbyname(socket.gethostname())
    PORT = 9000
    print(IP,":",PORT)
    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    soc.bind((IP, PORT))
    soc.listen(10)
    return soc
         
def run():
    
    ComPort = ConnectToDevice();    
    socket = StartTCP();
    
    result = SendCMDtoRS232(ComPort, "RLYON") 
    result = SendCMDtoRS232(ComPort, "SPKOFF") 
    print(result)
    
    print('Socket is listening..')
    while True:  
        conn, addr = socket.accept()
        ip, port = str(addr[0]), str(addr[1])
        print('Accepting connection from ' + ip + ':' + port)
        try:
            thread = Thread(target=DriveCommand, args=(conn, ip, port, ComPort))
            thread.start()
        except:
            print("Terible error!")    

    conn.close()
    ComPort.close()

if __name__ == "__main__":
    run()   