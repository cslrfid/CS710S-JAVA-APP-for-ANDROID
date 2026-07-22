package com.csl.cslibrary4a;

import static android.Manifest.permission.BLUETOOTH_CONNECT;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.bluetooth.le.ScanResult;

import androidx.core.app.ActivityCompat;

public class CsReaderConnector {
    String stringVersion = BuildConfig.VERSION_NAME;
    final boolean appendToLogViewDisable = false;
    final boolean DEBUG = false;
    boolean DEBUGTHREAD, DEBUG_APDATA, DEBUG_CONNECT, DEBUG_SCAN;

    public String getlibraryVersion() {
        String stringVersion = BuildConfig.VERSION_NAME;
        return stringVersion;
    }

    public boolean sameCheck = true;
    public void setSameCheck(boolean sameCheck1) {
        this.sameCheck = sameCheck1;
    }

    String byteArrayToString(byte[] packet) { return utility.byteArrayToString(packet); }
    void appendToLog(String s) { utility.appendToLog(s); }
    public boolean connect(ReaderDevice readerDevice) {
        boolean result = false, DEBUG = true;
        if (DEBUG) appendToLog("csReaderConnector.connect: readerDevice is " + (readerDevice == null ? "null" : "valid"));
        int iNumberColon = readerDevice.getAddress().split(":").length;
        int iNumberDot = readerDevice.getAddress().split("\\.").length;
        if (DEBUG) appendToLog("csReaderConnector.connect with split[:].length = " + iNumberColon + ", split[.].length = " + iNumberDot);
        if (DEBUG || DEBUG_CONNECT) appendToLog("csReaderConnector.connect(" + readerDevice.getAddress() + ")");
        if (iNumberColon == 6 && iNumberDot == 1) {
            BluetoothGatt.BluetoothGattDevice bluetoothGattDevice = new BluetoothGatt.BluetoothGattDevice();
            bluetoothGattDevice.address = readerDevice.getAddress();
            bluetoothGattDevice.name = readerDevice.getName();
            bluetoothGattDevice.isConnected = readerDevice.isConnected();
            result = bluetoothGatt.connect(bluetoothGattDevice); //for bluetooth
        }
        if (result) writeDataCount = 0;
        return result;
    }

    public boolean isConnected() {
        if (bluetoothGatt.isConnected()) return true;
        return false;
    }

    public void disconnect() {
        appendToLog("CsReaderConnector.disconnect: start");
        bluetoothGatt.disconnect();
        appendToLog("abcc done");
        if (rfidConnector != null) {
            rfidConnector.rfidToWrite.clear(); appendToLog("BtDataOut: mRfidToWrite.clear 1");
        }
        if (rfidReader != null) rfidReader.mRx000ToWrite.clear();
    }

    public long getStreamInRate() { return bluetoothGatt.getStreamInRate(); }

    int writeDataCount; int btSendTimeOut = 0; long btSendTime = 0; int BTSENDDELAY = 20;
    boolean writeData(byte[] buffer, int timeout) {
        appendToLog("CsReaderConnector.writeData: start with buffer = " + byteArrayToString(buffer));
        if (rfidReader.isInventoring()) {
            utility.appendToLogView("BtData: isInventoring is true when writeData " + byteArrayToString(buffer));
        }
        boolean result = false;
        result = bluetoothGatt.writeBleStreamOut(buffer);
        if (!result) appendToLog("CsReaderConnector.writeData: failure to writeData with previous btSendTimeout = " + btSendTimeOut + ", btSendTime = " + btSendTime);
        if (true) {
            byte[] bytes_reference = new byte[] { (byte)0xA7, (byte)0xB3, (byte)0xF0, (byte)0xC2, (byte)0x82, (byte)0x37, 0, 0, (byte)0x80, 3 };
            if (buffer.length > 10 + 4) {
                appendToLog("CsReaderConnector.writeData: going to check 8003 bytes_reference");
                int i = 0;
                for (; i < bytes_reference.length; i++) {
                    if (bytes_reference[i] != buffer[i]) break;
                }
                if (i == bytes_reference.length) {
                    byte[] buffer_abc = new byte[4];
                    System.arraycopy(buffer, 10, buffer_abc, 0, buffer_abc.length);
                    timeout += 1200;
                    if (buffer[10] == buffer[12] && buffer[11] == buffer[13]) {
                        appendToLog("CsReaderConnector.writeData: matched last sequences");
                        timeout += 10000;
                    }
                    appendToLog("CsReaderConnector.writeData: matched 8003 bytes_reference with buffer_abc = " + byteArrayToString(buffer_abc) + ", timeout = " + timeout);
                }
            }
            bytes_reference = new byte[] { (byte)0xA7, (byte)0xB3, (byte)0xF0, (byte)0xE8, (byte)0x82, (byte)0x37, 0, 0, (byte)0xB0, 1 };
            if (buffer.length > 10 + 4) {
                appendToLog("CsReaderConnector.writeData: going to check b001 bytes_reference");
                int i = 0;
                for (; i < bytes_reference.length; i++) {
                    appendToLog("CsReaderConnector.writeData: bytes_reference[" + i + "]=" + String.format("%02X", bytes_reference[i]) + ", buffer[" + i + "]=" + String.format("%02X", buffer[i]));
                    if (bytes_reference[i] != buffer[i]) break;
                }
                if (i == bytes_reference.length) {
                    byte[] buffer_abc = new byte[4];
                    System.arraycopy(buffer, 10, buffer_abc, 0, buffer_abc.length);
                    timeout += 1000;
                    if (buffer[10] == buffer[12] && buffer[11] == buffer[13]) {
                        appendToLog("CsReaderConnector.writeData: matched last sequences");
                        timeout += 9000;
                    }
                    appendToLog("CsReaderConnector.writeData: matched b001 bytes_reference with buffer_abc = " + byteArrayToString(buffer_abc) + ", timeout = " + timeout);
                }
            }
            bytes_reference = new byte[] { (byte)0xA7, (byte)0xB3, (byte)0xF0, (byte)0xE8, (byte)0x82, (byte)0x37, 0, 0, (byte)0xC0, 1 };
            if (buffer.length > 10 + 4) {
                appendToLog("CsReaderConnector.writeData: going to check b001 bytes_reference");
                int i = 0;
                for (; i < bytes_reference.length; i++) {
                     appendToLog("CsReaderConnector.writeData: bytes_reference[" + i + "]=" + String.format("%02X", bytes_reference[i]) + ", buffer[" + i + "]=" + String.format("%02X", buffer[i]));
                    if (bytes_reference[i] != buffer[i]) break;
                }
                if (i == bytes_reference.length) {
                    byte[] buffer_abc = new byte[4];
                    System.arraycopy(buffer, 10, buffer_abc, 0, buffer_abc.length);
                    timeout += 1000;
                    if (buffer[10] == buffer[12] && buffer[11] == buffer[13]) {
                        appendToLog("CsReaderConnector.writeData: matched last sequences");
                        timeout += 9000;
                    }
                    appendToLog("CsReaderConnector.writeData: matched b001 bytes_reference with buffer_abc = " + byteArrayToString(buffer_abc) + ", timeout = " + timeout);
                }
            }
        }
        if (true) {
            btSendTime = System.currentTimeMillis();
            btSendTimeOut = timeout + BTSENDDELAY;
            utility.appendToLog("CsReaderConnector.writeData: UsbConnector: btSendTimeOut 0 = " + btSendTimeOut);
        }
        utility.appendToLog("CsReaderConnector.writeData: UsbConnector: result = " + result);
        return result;
    }

    int[] crc_lookup_table = new int[]{
            0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
            0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
            0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
            0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
            0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
            0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
            0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
            0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
            0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
            0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
            0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
            0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
            0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
            0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
            0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
            0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
            0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
            0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
            0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
            0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
            0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
            0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
            0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
            0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
            0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
            0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
            0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
            0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
            0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
            0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
            0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
            0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78};

    boolean dataRead = false; int dataReadDisplayCount = 0; boolean mCs108DataReadRequest = false;
    int inventoryLength = 0;
    int iSequenceNumber; boolean bDifferentSequence = false, bFirstSequence = true;
    public int validata;
    public int[] invalidata = new int[2]; //invalidata = invalidata[0], invalidUpdata = invalidata[1];
    public void clearInvalidata() {
        invalidata[0] = 0;
        invalidata[1] = 0;
        validata = 0;
    }
    //boolean dataInBufferResetting;

    void processStreamInData() {
        final boolean DEBUG = false;
        int cs108DataReadStartOld = 0;
        int cs108DataReadStart = 0;
        boolean validHeader = false;

        if (DEBUG) appendToLog("CsReaderConnector.processStreamInData: starts with cs108DataLeft " + (cs108DataLeft == null ? "null" : "valid"));
        if (cs108DataLeft == null) return;
        int iStreamInBufferSize = 0;
        iStreamInBufferSize = bluetoothGatt.getStreamInBufferSize();
        if (DEBUG) appendToLog("CsReaderConnector.processStreamInData: iStreamInBufferSize = " + iStreamInBufferSize);
        //boolean bFirst = true;
        long lTime = System.currentTimeMillis();
        boolean bLooping = false;
        while (true) {
            iStreamInBufferSize = bluetoothGatt.getStreamInBufferSize();
            if (DEBUG) appendToLog("CsReaderConnector.processStreamInData: iStreamInBufferSize = " + iStreamInBufferSize);
            if (iStreamInBufferSize == 0) break;

            if (DEBUG) appendToLog("CsReaderConnector.processStreamInData: while loop starts");
            if (utility.DEBUG_FMDATA && bLooping == false) appendToLog("FmData: Enter loop with cs108DataLeftOffset=" + cs108DataLeftOffset + ", streamInBufferSize=" + iStreamInBufferSize);
            bLooping = true;

            if ((System.currentTimeMillis() - lTime > (bluetoothGatt.getIntervalProcessBleStreamInData()/2))) {
                appendToLog("CsReaderConnector.processStreamInData: timeout");
                utility.writeDebug2File("Up2  " + bluetoothGatt.getIntervalProcessBleStreamInData()/2 + "ms Timeout");
                utility.appendToLogView("FmData: Timeout !!!");
                break;
            }

            if (DEBUG) appendToLog("CsReaderConnector.processStreamInData: going to readData");
            int len = readData(cs108DataLeft, cs108DataLeftOffset, cs108DataLeft.length);
            if (DEBUG) appendToLog("CsReaderConnector.processStreamInData: readData len = " + len);
            if (utility.DEBUG_FMDATA && len != 0) {
                byte[] debugData = new byte[len];
                System.arraycopy(cs108DataLeft, cs108DataLeftOffset, debugData, 0, len);
                appendToLog("FmData: " + len + " dataIn = " + byteArrayToString(debugData));
            }
            //if (len != 0 && bFirst) { bFirst = false; } //writeDebug2File("B" + String.valueOf(getIntervalProcessBleStreamInData()) + ", " + System.currentTimeMillis()); }
            cs108DataLeftOffset += len;
            if (len == 0) {
                appendToLog("FmData: len is zero !!!");
                if (zeroLenDisplayed == false) {
                    zeroLenDisplayed = true;
                    if (true) {
                        if (bluetoothGatt.getStreamInTotalCounter() != bluetoothGatt.getStreamInAddCounter() || bluetoothGatt.getStreamInAddTime() != 0 || cs108DataLeftOffset != 0) {
                            if (DEBUG)
                                appendToLog("FmData: processCs108DataIn(" + bluetoothGatt.getStreamInTotalCounter() + "," + bluetoothGatt.getStreamInAddCounter() + "): len=0, getStreamInAddTime()=" + bluetoothGatt.getStreamInAddTime() + ", Offset=" + cs108DataLeftOffset);
                        }
                    }
                }
                if (cs108DataLeftOffset == cs108DataLeft.length) {
                    if (DEBUG) appendToLog("FmData: cs108DataLeftOffset=" + cs108DataLeftOffset + ", cs108DataLeft=" + byteArrayToString(cs108DataLeft));
                }
                break;
            } else {
                dataRead = true;
                zeroLenDisplayed = false;

                if (utility.DEBUG_FMDATA) appendToLog("FmData: cs108DataLeftOffset = " + cs108DataLeftOffset + ", cs108DataReadStart = " + cs108DataReadStart);
                while (cs108DataLeftOffset >= cs108DataReadStart + 8) {
                    if (DEBUG) appendToLog("FmData: while looping");
                    validHeader = false;
                    byte[] dataIn = cs108DataLeft;
                    int iPayloadLength = (dataIn[cs108DataReadStart + 2] & 0xFF);
                    if ((dataIn[cs108DataReadStart + 0] == (byte) 0xA7)
                            && (dataIn[cs108DataReadStart + 1] == (byte) 0xB3)
                            && (dataIn[cs108DataReadStart + 3] == (byte) 0xC2
                            || dataIn[cs108DataReadStart + 3] == (byte) 0x6A
                            || dataIn[cs108DataReadStart + 3] == (byte) 0xD9
                            || dataIn[cs108DataReadStart + 3] == (byte) 0xE8
                            || dataIn[cs108DataReadStart + 3] == (byte) 0x5F)
                            //&& ((dataIn[cs108DataReadStart + 4] == (byte) 0x82) || ((dataIn[cs108DataReadStart + 3] == (byte) 0xC2) && (dataIn[cs108DataReadStart + 8] == (byte) 0x81)))
                            && (dataIn[cs108DataReadStart + 5] == (byte) 0x9E)) {
                        if (DEBUG) appendToLog("FmData: matched header with cs108DataLeftOffset = " + cs108DataLeftOffset + ", cs108DataReadStart = " + cs108DataReadStart + ", iPayloadLength = " + iPayloadLength);
                        if (cs108DataLeftOffset - cs108DataReadStart < (iPayloadLength + 8))
                            break;

                        boolean bcheckChecksum = true;
                        int checksum = ((byte) dataIn[cs108DataReadStart + 6] & 0xFF) * 256 + ((byte) dataIn[cs108DataReadStart + 7] & 0xFF);
                        int checksum2 = 0;
                        if (bcheckChecksum) {
                            for (int i = cs108DataReadStart; i < cs108DataReadStart + 8 + iPayloadLength; i++) {
                                if (i != (cs108DataReadStart + 6) && i != (cs108DataReadStart + 7)) {
                                    int index = (checksum2 ^ ((byte) dataIn[i] & 0x0FF)) & 0x0FF;
                                    int table_value = crc_lookup_table[index];
                                    checksum2 = (checksum2 >> 8) ^ table_value;
                                }
                            }
                            if (DEBUG) appendToLog("FmData: checksum = " + String.format("%04X", checksum) + ", checksum2 = " + String.format("%04X", checksum2));
                        }
                        if (DEBUG) appendToLog("FmData: bcheckChecksum = " + bcheckChecksum + ", checksum = " + checksum + ", checksum2 = " + checksum2);
                        if (bcheckChecksum && checksum != checksum2) {
                            if (utility.DEBUG_FMDATA) {
                                if (iPayloadLength < 0) {
                                    appendToLog("FmData: CheckSum ERROR, iPayloadLength=" + iPayloadLength + ", cs108DataLeftOffset=" + cs108DataLeftOffset + ", dataIn=" + byteArrayToString(dataIn));
                                }
                                byte[] invalidPart = new byte[8 + iPayloadLength];
                                System.arraycopy(dataIn, cs108DataReadStart, invalidPart, 0, invalidPart.length);
                                appendToLog("FmData: processCs108DataIn_ERROR, INCORRECT RevChecksum=" + Integer.toString(checksum, 16) + ", CalChecksum2=" + Integer.toString(checksum2, 16) + ",data=" + byteArrayToString(invalidPart));
                            }
                        } else {
                            validHeader = true;
                            if (cs108DataReadStart > cs108DataReadStartOld) {
                                if (utility.DEBUG_FMDATA) {
                                    byte[] invalidPart = new byte[cs108DataReadStart - cs108DataReadStartOld];
                                    System.arraycopy(dataIn, cs108DataReadStartOld, invalidPart, 0, invalidPart.length);
                                    appendToLog("FmData: processCs108DataIn_ERROR, before valid data, invalid unused data: " + invalidPart.length + ", " + byteArrayToString(invalidPart));
                                }
                            } else if (cs108DataReadStart < cs108DataReadStartOld)
                                if (utility.DEBUG_FMDATA) appendToLog("FmData: processCs108DataIn_ERROR, invalid cs108DataReadStartdata=" + cs108DataReadStart + " < cs108DataReadStartOld=" + cs108DataReadStartOld);
                            cs108DataReadStartOld = cs108DataReadStart;

                            ConnectorData connectorData = new ConnectorData();
                            byte[] dataValues = new byte[iPayloadLength];
                            System.arraycopy(dataIn, cs108DataReadStart + 8, dataValues, 0, dataValues.length);
                            connectorData.dataValues = dataValues;
                            connectorData.milliseconds = System.currentTimeMillis();
                            if (utility.DEBUG_FMDATA) {
                                byte[] headerbytes = new byte[8];
                                System.arraycopy(dataIn, cs108DataReadStart, headerbytes, 0, headerbytes.length);
                                appendToLog("FmData: Got formatted dataIn = " + byteArrayToString(headerbytes) + " " + byteArrayToString(dataValues));
                            }
                            switch (dataIn[cs108DataReadStart + 3]) {
                                case (byte) 0xC2:
                                case (byte) 0x6A:
                                    if (dataIn[cs108DataReadStart + 3] == (byte) 0xC2) connectorData.connectorTypes = ConnectorData.ConnectorTypes.RFID;
                                    else connectorData.connectorTypes = ConnectorData.ConnectorTypes.BARCODE;
                                    if (dataIn[cs108DataReadStart + 8] == (byte) 0x81 || (bis108 == false && dataIn[cs108DataReadStart + 8] == (byte) 0x91)) {
                                        int iSequenceNumber = (int) (dataIn[cs108DataReadStart + 4] & 0xFF);
                                        int itemp = iSequenceNumber;
                                        if (itemp < this.iSequenceNumber) {
                                            itemp += 256;
                                        }
                                        itemp -= (this.iSequenceNumber + 1);
                                        if (DEBUG) appendToLog("FmData: iSequenceNumber = " + iSequenceNumber + ", old iSequenceNumber = " + this.iSequenceNumber + ", difference = " + itemp);
                                        if (itemp != 0) {
                                            if (DEBUG) appendToLog("FmData: Non-zero iSequenceNumber difference = " + itemp);
                                            connectorData.invalidSequence = true;
                                            if (bFirstSequence == false) {
                                                invalidata[0] += itemp;
                                                String stringSequenceList = "";
                                                for (int i = 0; i < itemp; i++) {
                                                    int iMissedNumber = (iSequenceNumber - i - 1);
                                                    if (iMissedNumber < 0) iMissedNumber += 256;
                                                    stringSequenceList += (i != 0 ? ", " : "") + String.format("%X", iMissedNumber);
                                                }
                                                if (DEBUG) utility.appendToLogView("FmData: " + String.format("ERROR !!!: %X - %X, miss %d: ", iSequenceNumber, this.iSequenceNumber, itemp) + stringSequenceList);
                                            }
                                        }
                                        bFirstSequence = false;
                                        this.iSequenceNumber = iSequenceNumber;
                                    }
                                    if (DEBUG) utility.appendToLogView("FmData: Rin: " + (connectorData.invalidSequence ? "invalid sequence" : "ok") + "," + byteArrayToString(connectorData.dataValues));
                                    validata++;
                                    break;
                                case (byte) 0xD9:
                                    if (DEBUG) appendToLog("FmData: BARTRIGGER NotificationData = " + byteArrayToString(connectorData.dataValues));
                                    connectorData.connectorTypes = ConnectorData.ConnectorTypes.NOTIFICATION;
                                    break;
                                case (byte) 0xE8:
                                    connectorData.connectorTypes = ConnectorData.ConnectorTypes.SILICONLAB;
                                    break;
                                case (byte) 0x5F:
                                    connectorData.connectorTypes = ConnectorData.ConnectorTypes.BLUETOOTH;
                                    break;
                            }
                            this.connectorDataList.add(connectorData);
                            if (utility.DEBUG_FMDATA) appendToLog("FmData: Got PackageIn with connectorData Type = " + connectorData.connectorTypes.toString() + ", Data = " + byteArrayToString(connectorData.dataValues));
                            utility.writeDebug2File("Up2  " + connectorData.connectorTypes.toString() + ", " + byteArrayToString(connectorData.dataValues));
                            cs108DataReadStart += ((8 + iPayloadLength));

                            byte[] cs108DataLeftNew = new byte[CS108DATALEFT_SIZE];
                            if (cs108DataLeftOffset - cs108DataReadStart < 0) {
                                if (utility.DEBUG_FMDATA) appendToLog("FmData: cs108DataLeftOffset = " + cs108DataLeftOffset + ", cs108DataReadStart = " + cs108DataReadStart + ", buffer = " + byteArrayToString(cs108DataLeft));
                                break;
                            }
                            System.arraycopy(cs108DataLeft, cs108DataReadStart, cs108DataLeftNew, 0, cs108DataLeftOffset - cs108DataReadStart);
                            cs108DataLeft = cs108DataLeftNew;
                            cs108DataLeftOffset -= cs108DataReadStart;
                            cs108DataReadStart = 0;
                            cs108DataReadStart = -1;
                            if (true || mCs108DataReadRequest == false) {
                                mCs108DataReadRequest = true;
                                if (DEBUGTHREAD && DEBUG) appendToLog("ready2Write: start immediate mReadWriteRunnable");
                                if (DEBUGTHREAD) appendToLog("CsReaderConnector.mReadWriteRunnable.run: post[mReadWriteRunnable]");
                                mHandler.removeCallbacks(mReadWriteRunnable); mHandler.post(mReadWriteRunnable);
                                if (utility.DEBUG_BTDATA && DEBUG) appendToLog("BtData: CsReaderConnector.processBleStreamOut starts mReadWriteRunnable as mCs108DataReadRequest");
                            } //appendToLog("BtData: processBleStreamOut cannot start mReadWriteRunnable as mCs108DataReadRequest is true");
                        }
                    }
                    if (DEBUG) appendToLog("FmData: going to loop again with validHeader = " + validHeader + ", cs108DataReadStart = " + cs108DataReadStart + ", cs108DataReadStartOld = " + cs108DataReadStartOld);
                    if (validHeader && cs108DataReadStart < 0) {
                        cs108DataReadStart = 0;
                        cs108DataReadStartOld = 0;
                    } else {
                        cs108DataReadStart++;
                    }
                }
                if (DEBUG) appendToLog("FmData: end of while loop with cs108DataReadStart = " + cs108DataReadStart + ", cs108DataLeftOffset = " + cs108DataLeftOffset);
                if (cs108DataReadStart != 0 && cs108DataLeftOffset >= 8) {
                    if (utility.DEBUG_FMDATA) {
                        byte[] invalidPart = new byte[cs108DataReadStart];
                        System.arraycopy(cs108DataLeft, 0, invalidPart, 0, invalidPart.length);
                        byte[] validPart = new byte[cs108DataLeftOffset - cs108DataReadStart];
                        System.arraycopy(cs108DataLeft, cs108DataReadStart, validPart, 0, validPart.length);
                        appendToLog("FmData: processCs108DataIn_ERROR, ENDLOOP invalid unused data: " + invalidPart.length + ", " + byteArrayToString(invalidPart) + ", with valid data length=" + validPart.length + ", " + byteArrayToString(validPart));
                        utility.writeDebug2File("Up2  Invalid " + invalidPart.length + ", " + byteArrayToString(invalidPart));
                    }

                    byte[] cs108DataLeftNew = new byte[CS108DATALEFT_SIZE];
                    System.arraycopy(cs108DataLeft, cs108DataReadStart, cs108DataLeftNew, 0, cs108DataLeftOffset - cs108DataReadStart);
                    cs108DataLeft = cs108DataLeftNew;
                    cs108DataLeftOffset -= cs108DataReadStart; cs108DataReadStart = 0;
                }
            }
        }
        if (DEBUG) appendToLog("CsReaderConnector.processStreamInData: while loop ending");
        if (utility.DEBUG_FMDATA && bLooping) appendToLog("FmData: Exit loop with cs108DataLeftOffset=" + cs108DataLeftOffset);
    }

    private int readData(byte[] buffer, int byteOffset, int byteCount) {
        return bluetoothGatt.readSteamIn(buffer, byteOffset, byteCount); }

    public class CsConnectorData {
        public int getVoltageMv() { return notificationConnector.mVoltageValue; }
        public int getVoltageCount() { return notificationConnector.mVoltageCount; }
        boolean getTriggerButtonStatus() { return notificationConnector.triggerButtonStatus; }
        public int getTriggerCount() { return notificationConnector.iTriggerCount; }
        Date timeStamp;
        String getTimeStamp() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            return sdf.format(csConnectorData.timeStamp);
        }

    }
    public CsConnectorData csConnectorData;
    public SettingData settingData;

    public RfidConnector rfidConnector; public RfidReader rfidReader;
    public BarcodeConnector barcodeConnector; public BarcodeNewland barcodeNewland;
    public NotificationConnector notificationConnector;
    public ControllerConnector controllerConnector;
    public BluetoothConnector bluetoothConnector;

    private Handler mHandler = new Handler();

    public void csConnectorDataInit() {
        connectorDataList = new ArrayList<>();
        cs108DataLeft = new byte[CS108DATALEFT_SIZE];
        cs108DataLeftOffset = 0;
        zeroLenDisplayed = false;

        clearInvalidata();
        writeDataCount = 0;

        csConnectorData = new CsConnectorData();
        notificationConnector = new NotificationConnector(context, utility, settingData.triggerReporting, settingData.triggerReportingCountSetting);
        controllerConnector = new ControllerConnector(context, utility);
        bluetoothConnector = new BluetoothConnector(context, utility, settingData.userDebugEnable, bis108);
        //settingData = new SettingData(context, utility);

        rfidReader = new RfidReader(context, utility, bluetoothGatt, settingData, bis108, intervalRx000UplinkHandler);
        rfidConnector = rfidReader.rfidConnector;
        barcodeConnector = new BarcodeConnector(context, utility);
        barcodeNewland = new BarcodeNewland(context, utility, barcodeConnector, settingData.barcode2TriggerMode);
        barcodeConnector.barcodeConnectorCallback = new BarcodeConnector.BarcodeConnectorCallback(){
            @Override
            public int callbackMethod(byte[] dataValues, BarcodeConnector.CsReaderBarcodeData csReaderBarcodeData) {
                return barcodeNewland.decodeBarcodeUplinkData(dataValues, csReaderBarcodeData);
            }
        };
        settingData.setConnectedConnectors(notificationConnector, rfidReader);
        mHandler.removeCallbacks(mReadWriteRunnable); mHandler.post(mReadWriteRunnable);
        appendToLog("!!! all major classes are initialised");
    }

    final int CS108DATALEFT_SIZE = 300; //4000;    //100;
    private ArrayList<ConnectorData> connectorDataList;
    byte[] cs108DataLeft;
    int cs108DataLeftOffset;
    boolean zeroLenDisplayed;

    public BluetoothGatt bluetoothGatt;
    public ScanCallback mScanCallback = null;
    public BluetoothAdapter.LeScanCallback mLeScanCallback = null;
    Context context; TextView mLogView; public Utility utility; boolean bis108; int iScanType;
    public void setScanType(int iScanType) { this.iScanType = iScanType; }
    public CsReaderConnector(Context context, TextView mLogView, Utility utility, boolean bis108) {
        this.context = context;
        this.mLogView = mLogView;
        this.utility = utility;
        this.bis108 = bis108;
        this.iScanType = iScanType;
        appendToLog("CsReaderConnector.CsReaderConnector with bis108 = " + (bis108 ? "true" : "false"));

        DEBUGTHREAD = utility.DEBUGTHREAD; DEBUG_APDATA = utility.DEBUG_APDATA;
        DEBUG_CONNECT = utility.DEBUG_CONNECT; DEBUG_SCAN = utility.DEBUG_SCAN;

        if (true) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    if (DEBUG) appendToLog("onBatchScanResults()");
                }

                @Override
                public void onScanFailed(int errorCode) {
                    if (DEBUG) appendToLog("onScanFailed()");
                }

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    boolean DEBUG = true;
                    if (true) {
                        BluetoothGatt.CsScanData scanResultA = new BluetoothGatt.CsScanData(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                        boolean found98 = true;
                        if (true) {
                            CheckResult resultc = check9800(scanResultA);
                            if (resultc != null) {
                                found98 = resultc.is980;
                                scanResultA.serviceUUID2p2 = resultc.serviceUUID2p2;
                                scanResultA.hasServicePower = resultc.hasPowerLevel;
                                scanResultA.name = resultc.stringName;
                            }
                        }
                        if (DEBUG) appendToLog("CsReaderConnector " + bis108 + ", found98 = " + found98 + ", mScanResultList 0 = " + (mScanResultList != null ? "VALID" : "NULL"));
                        if (mScanResultList != null && found98) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                if (ActivityCompat.checkSelfPermission(context, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;
                            } else if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) return;
                            mScanResultList.add(scanResultA);
                            if (DEBUG) appendToLog("CsReaderConnector, bis108 = " + bis108 + ", mScanResultList 0 = " + mScanResultList.size() + ", serviceUUID2p2 = " + scanResultA.serviceUUID2p2);
                        }
                    }
                }
            };
        }

        bluetoothGatt = new BluetoothGatt(context, utility, (bis108 ? "9800" : "9802"));
        bluetoothGatt.connectorCallback = new BluetoothGatt.ConnectorCallback() {
            @Override
            public void callbackMethod() {
                //appendToLog("going to processBleStreamInData with bis108 " + bis108 + " and connected " + isBleConnected());
                processStreamInData();
            }
        };

        //cs108ConnectorDataInit();
        //mHandler.removeCallbacks(bluetoothGatt.runnableProcessBleStreamInData); mHandler.post(bluetoothGatt.runnableProcessBleStreamInData);
        //if (DEBUGTHREAD) appendToLog("start immediate mReadWriteRunnable");
        //mHandler.removeCallbacks(mReadWriteRunnable); mHandler.post(mReadWriteRunnable);
        //mHandler.removeCallbacks(runnableRx000UplinkHandler); mHandler.post(runnableRx000UplinkHandler);
        appendToLog("foregroundReader: new SettingData for bis108 as " + bis108);
        settingData = new SettingData(context, utility);
    }

    public BluetoothGatt.CsScanData getNewDeviceScanned() {
        if (!mScanResultList.isEmpty()) {
            if (DEBUG_SCAN) appendToLog("mScanResultList.size() = " + mScanResultList.size());
            BluetoothGatt.CsScanData csScanData0 = mScanResultList.get(0); mScanResultList.remove(0);
            if (csScanData0 != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
            }
            return csScanData0;
        } else return null;
    }
    ArrayList<BluetoothGatt.CsScanData> mScanResultList = new ArrayList<>();
    class CheckResult {
        boolean is980;
        int serviceUUID2p2;
        boolean hasPowerLevel;
        String stringName;
    }
    CheckResult check9800(BluetoothGatt.CsScanData scanResultA) {
        boolean found98 = false, DEBUG = true;
        if (DEBUG) appendToLog("decoded data size = " + scanResultA.decoded_scanRecord.size());
        int iNewADLength = 0;
        byte[] newAD = new byte[0];
        int iNewADIndex = 0;
        if (bluetoothGatt.isBLUETOOTH_CONNECTinvalid()) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            return null;
        CheckResult checkResult = new CheckResult(); checkResult.is980 = false; checkResult.serviceUUID2p2 = -1; checkResult.hasPowerLevel = false;
        String strTemp = scanResultA.device.getName();
        if (strTemp != null && DEBUG)
            appendToLog("Found name = " + strTemp + ", length = " + String.valueOf(strTemp.length()));
        boolean hasPowerLevel = false;
        for (byte bdata : scanResultA.scanRecord) {
            if (iNewADIndex >= iNewADLength && iNewADLength != 0) {
                scanResultA.decoded_scanRecord.add(newAD);
                iNewADIndex = 0;
                iNewADLength = 0;
                if (DEBUG) {
                    String stringADType = "";
                    if (newAD[0] == 1) stringADType = "Flags";
                    else if (newAD[0] == 2) stringADType = "Incomplete List of 16-bit Service UUIDs";
                    else if (newAD[0] == 3) stringADType = "Complete list of 16-bit service UUIDs";
                    else if (newAD[0] == 9) {
                        byte[] byteString = new byte[newAD.length - 1];
                        System.arraycopy(newAD, 1, byteString, 0, byteString.length);
                        checkResult.stringName = new String(byteString, StandardCharsets.UTF_8);
                        stringADType = "Complete local name, " + checkResult.stringName;
                    }
                    else if (newAD[0] == 0x0A) {
                        stringADType = "Tx power level";
                        checkResult.hasPowerLevel = true;
                    } else if (newAD[0] == 0x12) stringADType = "Peripheral connection interval range";
                    else if (newAD[0] == 0x16) stringADType = "Service data - 16-bit UUID";
                    else if (newAD[0] == 0x19) stringADType = "Appearance";
                    else if (newAD[0] == (byte)0xFF) {
                        String stringManufactuer = "Unknown";
                        if (newAD[2] == 0x00 && newAD[1] == 0x06) stringManufactuer = "Microsoft";
                        else if (newAD[2] == 0x00 && newAD[1] == 0x4C) stringManufactuer = "Apple";
                        stringADType = stringManufactuer + " manufacturer specific Data";
                    }
                    else stringADType = "Unhandled";
                    appendToLog("Size = " + scanResultA.decoded_scanRecord.size() + ", " + byteArrayToString(newAD) + ", " + stringADType);
                }
            }
            if (iNewADLength == 0) {
                iNewADLength = bdata;
                newAD = new byte[iNewADLength];
                iNewADIndex = 0;
            } else newAD[iNewADIndex++] = bdata;
        }
        if (DEBUG) appendToLog("decoded data size = " + scanResultA.decoded_scanRecord.size());
        for (int i = 0; /*scanResultA.device.getType() == BluetoothDevice.DEVICE_TYPE_LE &&*/ i < scanResultA.decoded_scanRecord.size(); i++) {
            byte[] currentAD = scanResultA.decoded_scanRecord.get(i);
            if (DEBUG) appendToLog("Processing decoded data = " + byteArrayToString(currentAD));
            if (currentAD[0] == 2) {
                if (DEBUG) appendToLog("Processing UUIDs 0" + (bis108 ? "0" : "2"));
                if (false) {
                    found98 = true;
                    checkResult.serviceUUID2p2 = 2;
                } else if (((currentAD[1] == 0 && ((iScanType & 0x01) != 0)) || (currentAD[1] == 2 && ((iScanType & 0x02) != 0))) && currentAD[2] == (byte) 0x98) {
                    if (DEBUG) appendToLog("Found 980" + (bis108 ? "0" : "2"));
                    found98 = true;
                    checkResult.serviceUUID2p2 = currentAD[1];
                    break;
                }
            }
        }
        if (found98 == false && DEBUG)
            appendToLog("No 9800: with scanData = " + byteArrayToString(scanResultA.scanRecord));
        else if (DEBUG_SCAN)
            appendToLog("CsReaderConnector " + bis108 + ", Found 9800: with scanData = " + byteArrayToString(scanResultA.scanRecord));
        checkResult.is980 = found98;
        return checkResult;
    }

    long timeReady; boolean aborting = false, sendFailure = false;
    private final Runnable mReadWriteRunnable = new Runnable() {
        boolean ready2Write = false, DEBUG = false;
        int timer2Write = 0;
        boolean validBuffer;

        @Override
        public void run() {
            if (DEBUGTHREAD || utility.DEBUG_BTDATA) appendToLog("CsReaderConnector.mReadWriteRunnable.run starts");
            if (rfidConnector == null || barcodeConnector == null) {
                if (DEBUGTHREAD) appendToLog("CsReaderConnector.mReadWriteRunnable.run: 1, postDelayed[mReadWriteRunnable, 500]");
                mHandler.postDelayed(mReadWriteRunnable, 500);
                if (utility.DEBUG_BTDATA) appendToLog("BtData: CsReaderConnector.mReadWriteRunnable restart after 500ms");
                return;
            }
            if (timer2Write != 0 || bluetoothGatt.getStreamInBufferSize() != 0 || rfidConnector.rfidToRead.size() != 0) {
                validBuffer = true;
                if (DEBUG) appendToLog("mReadWriteRunnable(): START, timer2Write=" + timer2Write + ", streamInBufferSize = " + bluetoothGatt.getStreamInBufferSize() + ", mRfidToRead.size=" + rfidConnector.rfidToRead.size() + ", mRx000ToRead.size=" + rfidReader.mRx000ToRead.size());
            } else  validBuffer = false;
            int intervalReadWrite = 250;
            if (rfidConnector.rfidPowerOnTimeOut >= intervalReadWrite) {
                rfidConnector.rfidPowerOnTimeOut -= intervalReadWrite;
                if (rfidConnector.rfidPowerOnTimeOut <= 0) {
                    rfidConnector.rfidPowerOnTimeOut = 0;
                }
            }
            if (barcodeConnector.barcodePowerOnTimeOut >= intervalReadWrite) {
                barcodeConnector.barcodePowerOnTimeOut -= intervalReadWrite;
                if (barcodeConnector.barcodePowerOnTimeOut <= 0) {
                    barcodeConnector.barcodePowerOnTimeOut = 0;
                }
            }
            if (barcodeConnector.barcodePowerOnTimeOut != 0)
                if (DEBUG) appendToLog("mReadWriteRunnable(): barcodePowerOnTimeOut = " + barcodeConnector.barcodePowerOnTimeOut);

            long lTime = System.currentTimeMillis();
            if (DEBUGTHREAD) appendToLog("start new mReadWriteRunnable after " + intervalReadWrite + " ms");
            if (DEBUGTHREAD) appendToLog("CsReaderConnector.mReadWriteRunnable.run: 2, postDelayed[mReadWriteRunnable, interval=" + intervalReadWrite);
            mHandler.removeCallbacks(mReadWriteRunnable); mHandler.postDelayed(mReadWriteRunnable, intervalReadWrite);
            if (false && utility.DEBUG_BTDATA) appendToLog("BtData: CsReaderConnector.mReadWriteRunnable restart after 250ms");
            if (rfidReader == null) return;

            boolean bFirst = true;
            boolean bLooping = false;
            mCs108DataReadRequest = false;
            while (!connectorDataList.isEmpty()) {
                if (utility.DEBUG_PKDATA && bLooping == false) appendToLog("PkData: Entering loop with connectorDataList.size = " + connectorDataList.size());
                bLooping = true;

                if (!isConnected()) {
                    connectorDataList.clear();
                } else if (System.currentTimeMillis() - lTime > (intervalRx000UplinkHandler / 2)) {
                    utility.writeDebug2File("Up3  " + "Timeout");
                    utility.appendToLogView("PkData: mReadWriteRunnable: TIMEOUT !!! mCs108DataRead.size() = " + connectorDataList.size());
                    break;
                } else {
                    if (bFirst) { bFirst = false; } //writeDebug2File("C" + String.valueOf(intervalReadWrite) + ", " + System.currentTimeMillis()); }
                    try {
                        ConnectorData connectorData = connectorDataList.get(0);
                        connectorDataList.remove(0);
                        boolean bValid = true;
                        if (utility.DEBUG_PKDATA) appendToLog("PkData: connectorData.type = " + connectorData.connectorTypes.toString() + ", connectorData.dataValues = " + byteArrayToString(connectorData.dataValues));
                        if (rfidConnector.isMatchRfidToWrite(connectorData)) {
                            if (false) {
                                for (int i = 0; i < rfidReader.mRx000ToRead.size(); i++) {
                                    if (rfidReader.mRx000ToRead.get(i).responseType == RfidReaderChipData.HostCmdResponseTypes.TYPE_COMMAND_END)
                                        if (DEBUG) appendToLog("mRx0000ToRead with COMMAND_END is removed");
                                }
                                if (DEBUG) appendToLog("mRx000ToRead.clear !!!");
                            }
                            rfidReader.mRx000ToRead.clear(); if (DEBUG) appendToLog("mRx000ToRead.clear !!!");
                            if (writeDataCount > 0) writeDataCount--; if (bis108) ready2Write = true; //btSendTime = 0; aborting = false;
                        } else if (barcodeConnector.isMatchBarcodeToWrite(connectorData)) {
                            if (writeDataCount > 0) writeDataCount--; if (bis108) ready2Write = true; if (true) appendToLog("true isMatchBarcodeToWrite "); //btSendTime = 0;
                        } else if (notificationConnector.isMatchNotificationToWrite(connectorData)) {
                            if (writeDataCount > 0) writeDataCount--; ready2Write = true; if (true) appendToLog("true isMatchNotificationToWrite"); btSendTime = 0; if (utility.DEBUG_PKDATA) appendToLog("PkData: mReadWriteRunnable: matched notification. btSendTime is set to 0 to allow new sending.");
                        } else if (controllerConnector.isMatchControllerToWrite(connectorData)) {
                            if (writeDataCount > 0) writeDataCount--; ready2Write = true; if (false) appendToLog("true isMatchSiliconLabIcToWrite"); //btSendTime = 0; if (utility.DEBUG_PKDATA) appendToLog("PkData: mReadWriteRunnable: matched AtmelIc. btSendTime is set to 0 to allow new sending.");
                        } else if (bluetoothConnector.isMatchBluetoothIcToWrite(connectorData)) {
                            if (writeDataCount > 0) writeDataCount--; ready2Write = true; appendToLog("ready2Write is set true after true isMatchBluetoothIcToWrite "); btSendTime = 0; if (utility.DEBUG_PKDATA) appendToLog("PKData: mReadWriteRunnable: matched bluetoothIc. btSendTime is set to 0 to allow new sending.");
                        } else if (rfidConnector.isRfidToRead(connectorData)) { rfidConnector.rfidValid = true;
                        } else if (barcodeConnector.isBarcodeToRead(connectorData)) {
                        } else if (notificationConnector.isNotificationToRead(connectorData)) {
                            /* if (mRfidDevice.mRfidToWrite.size() != 0 && mNotificationDevice.mNotificationToRead.size() != 0) {
                                mNotificationDevice.mNotificationToRead.remove(0);
                                mRfidDevice.mRfidToWrite.clear();
                                mSiliconLabIcDevice.mSiliconLabIcToWrite.add(SiliconLabIcPayloadEvents.RESET);

                                timeReady = System.currentTimeMillis() - 1500;
                                appendToLog("mReadWriteRunnable: endingMessage: changed timeReady");
                            }*/
                        } else bValid = false;
                        if (bValid) {
                            //writeDebug2File("Up33 " + cs108ReadData.cs108ConnectedDevices.toString() + ", " + byteArrayToString(cs108ReadData.dataValues));
                        } else {
                            appendToLog("mReadWriteRunnable: !!! CANNOT process " + byteArrayToString(connectorData.dataValues) + " with mDataToWriteRemoved = " + barcodeConnector.mDataToWriteRemoved);
                            utility.writeDebug2File("Up3  Invalid " + connectorData.dataValues.length + ", " + byteArrayToString(connectorData.dataValues));
                        }
                        if (barcodeConnector.mDataToWriteRemoved)  {
                            barcodeConnector.mDataToWriteRemoved = false; ready2Write = true; btSendTime = 0;
                            appendToLog("ready2Write is set true after true mBarcodeDevice.mDataToWriteRemoved ");
                            if (utility.DEBUG_PKDATA) appendToLog("PkData: mReadWriteRunnable: processed barcode. btSendTime is set to 0 to allow new sending.");
                        }
                    } catch (Exception ex) {
                    }
                }
            }
            if (utility.DEBUG_PKDATA && bLooping) appendToLog("PkData: Exiting loop with connectorDataList.size = " + connectorDataList.size());

            lTime = System.currentTimeMillis();
            if (rfidConnector.mRfidToWriteRemoved || controllerConnector.mControllerToWriteRemoved || bluetoothConnector.dataToRemoved)  {
                if (rfidConnector.mRfidToWriteRemoved) rfidConnector.mRfidToWriteRemoved = false;
                if (controllerConnector.mControllerToWriteRemoved) controllerConnector.mControllerToWriteRemoved = false;
                if (bluetoothConnector.dataToRemoved) bluetoothConnector.dataToRemoved = false;
                ready2Write = true; btSendTime = 0; if (false) appendToLog("ready2Write is set true after true mRfidDevice.mRfidToWriteRemoved ");
                btSendTime = (lTime - btSendTimeOut + BTSENDDELAY);
                if (DEBUGTHREAD) appendToLog("ready2Write: start new mReadWriteRunnable after " + BTSENDDELAY + " ms");
                if (DEBUGTHREAD) appendToLog("CsReaderConnector.mReadWriteRunnable.run: 3, postDelayed[mReadWriteRunnable, interval=" + (BTSENDDELAY + 2));
                mHandler.removeCallbacks(mReadWriteRunnable); mHandler.postDelayed(mReadWriteRunnable, BTSENDDELAY + 2);
                if (utility.DEBUG_BTDATA) appendToLog("CsReaderConnector.mReadWriteRunnable restart after " + (BTSENDDELAY + 2) +"ms") ;
                if (utility.DEBUG_PKDATA) appendToLog("PkData: mReadWriteRunnable: processed Rfidcode. btSendTime is set to 0 to allow new sending with systime = " + lTime);
            }
            if (bis108) {
                int timeout2Ready = 2000;
                if (aborting || sendFailure) timeout2Ready = 200;
                if (System.currentTimeMillis() > timeReady + timeout2Ready) ready2Write = true;
            } else {
                if (ready2Write == false && lTime - btSendTime > btSendTimeOut) {
                    appendToLog("ready2Write is set to true from false with difference = " + (lTime - btSendTime) + ", systime = " + lTime + ", btSendTime = " + btSendTime + ", btSendTimeOut = " + btSendTime);
                    ready2Write = true;
                }
            }
            if (DEBUG) appendToLog("BtData: ready2Write = " + ready2Write);
            if (ready2Write) {
                timeReady = System.currentTimeMillis();
                timer2Write = 0;
                if (rfidConnector.rfidFailure) {
                    rfidConnector.rfidToWrite.clear(); appendToLog("BtDataOut: mRfidToWrite.clear 2");
                }
                //if (barcodeConnector.barcodeFailure) { barcodeConnector.barcodeToWrite.clear(); appendToLog("barcodeToWrite is clear"); }
                if (rfidReader.mRx000ToWrite.size() != 0 && rfidConnector.rfidToWrite.size() == 0) {
                    if (DEBUG)
                        appendToLog("mReadWriteRunnable(): mRx000ToWrite.size=" + rfidReader.mRx000ToWrite.size() + ", mRfidToWrite.size=" + rfidConnector.rfidToWrite.size());
                    rfidReader.addRfidToWrite(rfidReader.mRx000ToWrite.get(0));
                }
                boolean bisRfidCommandStop = false, bisRfidCommandExecute = false;
                if (rfidConnector.rfidToWrite.size() != 0 && DEBUG)
                    appendToLog("mRfidToWrite = " + rfidConnector.rfidToWrite.get(0).rfidPayloadEvent.toString() + "." + byteArrayToString(rfidConnector.rfidToWrite.get(0).dataValues) + ", ready2write = " + ready2Write);
                if (rfidConnector.rfidToWrite.size() != 0) {
                    RfidConnector.CsReaderRfidData csReaderRfidData = rfidConnector.rfidToWrite.get(0);
                    if (csReaderRfidData.rfidPayloadEvent == RfidConnector.RfidPayloadEvents.RFID_COMMAND) {
                        int ii;
                        if (false) {
                            byte[] byCommandExeccute = new byte[]{0x70, 1, 0, (byte) 0xF0};
                            for (ii = 0; ii < 4; ii++) {
                                if (byCommandExeccute[ii] != csReaderRfidData.dataValues[ii]) break;
                            }
                            if (ii == 4) bisRfidCommandExecute = true;
                        }

                        byte[] byCommandStop = new byte[]{(byte) 0x40, 3, 0, 0, 0, 0, 0, 0};
                        for (ii = 0; ii < 4; ii++) {
                            if (byCommandStop[ii] != csReaderRfidData.dataValues[ii]) break;
                        }
                        if (ii == 4) bisRfidCommandStop = true;
                        if (DEBUG)
                            appendToLog("mRfidToWrite(0).dataValues = " + byteArrayToString(rfidConnector.rfidToWrite.get(0).dataValues) + ", bisRfidCommandExecute = " + bisRfidCommandExecute + ", bisRfidCommandStop = " + bisRfidCommandStop);
                    }
                }
                if (barcodeConnector.barcodeToWrite.size() != 0 && true)
                    appendToLog("AAA 1 barcodeToWrite.size = " + barcodeConnector.barcodeToWrite.size() + ", bisRfidCommandStop = " + bisRfidCommandStop + ", barcodePowerOnTimeOut = " + barcodeConnector.barcodePowerOnTimeOut);
                if (false) appendToLog("CsReaderConnector.mReadWriteRunnable.run: BtDataOut, bisRfidCommandStop is " + bisRfidCommandStop
                        + ", bis108 = " + bis108 + ", isInventoring = " + rfidReader.isInventoring()
                        + ", rfidFailure = " + rfidConnector.rfidFailure + ", mRfidToWrite.size = " + rfidConnector.rfidToWrite.size() + ", rfidPowerOnTimeOut = " + rfidConnector.rfidPowerOnTimeOut);
                if (bisRfidCommandStop) {
                    if (rfidConnector.rfidPowerOnTimeOut != 0) {
                        if (DEBUG) appendToLog("rfidPowerOnTimeOut = " + rfidConnector.rfidPowerOnTimeOut + ", mRfidToWrite.size() = " + rfidConnector.rfidToWrite.size());
                    } else if (rfidConnector.rfidFailure == false && rfidConnector.rfidToWrite.size() != 0) {
                        if (isConnected() == false) {
                            rfidConnector.rfidToWrite.clear(); appendToLog("BtDataOut: mRfidToWrite.clear 3");
                        } else {
                            if (utility.DEBUG_BTDATA) appendToLog("BtData: CsReaderConnector.mReadWriteRunnable 1: currentTime = " + System.currentTimeMillis() + ", btSendTime = " + btSendTime + ", difference = " + (System.currentTimeMillis() - btSendTime) + ", btSendTimeOut = " + btSendTimeOut);
                            if (System.currentTimeMillis() - btSendTime > btSendTimeOut) {
                                boolean retValue = false;
                                appendToLog("aabb 5A");
                                byte[] dataOut = rfidConnector.sendRfidToWrite(false);
                                if (dataOut != null) {
                                    appendToLog("CsReaderConnector.mReadWriteRunnable.run: BtDataOut, 1 going to writeData with dataOut = " + byteArrayToString(dataOut));
                                    retValue = writeData(dataOut, (rfidConnector.rfidToWrite.get(0).waitUplinkResponse ? 500 : 0));
                                    if (false) appendToLog("BtData: done writeData with waitUplinkResponse = " + rfidConnector.rfidToWrite.get(0).waitUplinkResponse);
                                }
                                appendToLog("BtData: done writeRfid with size = " + rfidConnector.rfidToWrite.size() + ", PayloadEvents = " + rfidConnector.rfidToWrite.get(0).rfidPayloadEvent.toString() + ", data=" + byteArrayToString(rfidConnector.rfidToWrite.get(0).dataValues));
                                rfidConnector.sendRfidToWriteSent++;
                                if (retValue)   {
                                    rfidConnector.mRfidToWriteRemoved = false;
                                    if (DEBUG) appendToLog("writeRfid() with sendRfidToWriteSent = " + rfidConnector.sendRfidToWriteSent);
                                    sendFailure = false;
                                    //bValue = true;
                                } else sendFailure = true;
                                ready2Write = false;    //
                                appendToLog("ready2Write is set false after sendRfidToWrite");
                            }
                        }
                    }
                } else if (!bis108 && rfidReader.isInventoring()) {
                    if (rfidConnector.rfidPowerOnTimeOut != 0) {
                        if (DEBUG) appendToLog("rfidPowerOnTimeOut = " + rfidConnector.rfidPowerOnTimeOut + ", mRfidToWrite.size() = " + rfidConnector.rfidToWrite.size());
                    } else if (rfidConnector.rfidFailure == false && rfidConnector.rfidToWrite.size() != 0) {
                        if (isConnected() == false) {
                            rfidConnector.rfidToWrite.clear(); appendToLog("BtDataOut: mRfidToWrite.clear 4");
                        } else {
                            if (DEBUG)
                                appendToLog("BtDataOut 2: currentTime = " + System.currentTimeMillis() + ", btSendTime = " + btSendTime + ", difference = " + (System.currentTimeMillis() - btSendTime) + ", btSendTimeOut = " + btSendTimeOut);
                            if (System.currentTimeMillis() - btSendTime > btSendTimeOut) {
                                boolean retValue = false;
                                appendToLog("aabb 5b");
                                byte[] dataOut = rfidConnector.sendRfidToWrite(false);
                                if (dataOut != null) {
                                    appendToLog("CsReaderConnector.mReadWriteRunnable.run: BtDataOut, 2 going to writeData with dataOut = " + byteArrayToString(dataOut));
                                    retValue = writeData(dataOut, (rfidConnector.rfidToWrite.get(0).waitUplinkResponse ? 500 : 0));
                                    if (false) appendToLog("done writeData with waitUplinkResponse = " + rfidConnector.rfidToWrite.get(0).waitUplinkResponse);
                                }
                                if (DEBUG) appendToLog("BtDataOut: done writeRfid with size = " + rfidConnector.rfidToWrite.size() + ", PayloadEvents = " + rfidConnector.rfidToWrite.get(0).rfidPayloadEvent.toString() + ", data=" + byteArrayToString(rfidConnector.rfidToWrite.get(0).dataValues));
                                rfidConnector.sendRfidToWriteSent++;
                                if (retValue)   {
                                    rfidConnector.mRfidToWriteRemoved = false;
                                    if (DEBUG) appendToLog("writeRfid() with sendRfidToWriteSent = " + rfidConnector.sendRfidToWriteSent);
                                    sendFailure = false;
                                    //bValue = true;
                                } else sendFailure = true;

                                if (retValue) {
                                    ready2Write = false;
                                    if (false) appendToLog("ready2Write is set false after true sendRfidToWrite");
                                }
                            }
                        }
                    }
                } else if (notificationConnector.notificationToWrite.size() != 0) {
                    appendToLog("aabb 3n");
                    if (isConnected() == false) {
                        notificationConnector.notificationToWrite.clear(); appendToLog("notificationToWrite is clear"); }
                    else if (System.currentTimeMillis() - btSendTime > btSendTimeOut) {
                        appendToLog("aabb 3n1");
                        byte[] dataOut = notificationConnector.sendNotificationToWrite(false);
                        boolean retValue = false;

                        if (utility.DEBUG_PKDATA && notificationConnector.sendDataToWriteSent != 0)
                            appendToLog("!!! notificationToWrite.sendDataToWriteSent = " + controllerConnector.sendControllerToWriteSent);
                        if (utility.DEBUG_PKDATA)
                            appendToLog(String.format("PkData: write notificationToWrite.%s with notificationConnector.sendDataToWriteSent = %d",
                                    notificationConnector.notificationToWrite.get(0).notificationPayloadEvent.toString(),
                                    notificationConnector.sendDataToWriteSent));
                        if (false && notificationConnector.sendDataToWriteSent != 0)
                            appendToLog("!!! mSiliconLabIcDevice.sendDataToWriteSent = " + notificationConnector.sendDataToWriteSent);

                        if (dataOut != null) {
                            appendToLog("aabb 3n2");
                            appendToLog("CsReaderConnector.mReadWriteRunnable.run: BtDataOut, 3 going to writeData with dataOut = " + byteArrayToString(dataOut));
                            retValue = writeData(dataOut, 0);
                        }
                        if (retValue) {
                            //notificationController.sendDataToWriteSent++;
                        } else {
                            //if (DEBUG) appendToLogView("failure to send " + notificationController.notificationToWrite.get(0).toString());
                            //notificationController.notificationToWrite.remove(0); notificationController.sendDataToWriteSent = 0; appendToLog("notificationToWrite remove0 with length = " + notificationToWrite.size());
                        }
                    }
                    ready2Write = false;    //
                    if (false) appendToLog("ready2Write is set false after true sendSiliconLabIcToWrite");
                } else if (controllerConnector.controllerToWrite.size() != 0) {
                    appendToLog("AAA 5");
                    if (isConnected() == false) controllerConnector.controllerToWrite.clear();
                    else if (System.currentTimeMillis() - btSendTime > btSendTimeOut) {
                        byte[] dataOut = controllerConnector.sendControllerToWrite(false);
                        boolean retValue = false;

                        if (utility.DEBUG_PKDATA && controllerConnector.sendControllerToWriteSent != 0)
                            appendToLog("!!! siliconLabIcDevice.sendDataToWriteSent = " + controllerConnector.sendControllerToWriteSent);
                        if (utility.DEBUG_PKDATA)
                            appendToLog(String.format("PkData: write mSiliconLabIcDevice.%s with mSiliconLabIcDevice.sendDataToWriteSent = %d",
                                    controllerConnector.controllerToWrite.get(0).toString(),
                                    controllerConnector.sendControllerToWriteSent));
                        if (false && controllerConnector.sendControllerToWriteSent != 0)
                            appendToLog("!!! mSiliconLabIcDevice.sendDataToWriteSent = " + controllerConnector.sendControllerToWriteSent);

                        if (dataOut != null) {
                            appendToLog("CsReaderConnector.mReadWriteRunnable.run: 4 going to writeData with dataOut = " + byteArrayToString(dataOut));
                            retValue = writeData(dataOut, 0);
                        }
                        if (retValue) {
                            //controllerConnector.sendDataToWriteSent++;
                        } else {
                            //if (DEBUG) appendToLogView("failure to send " + controllerConnector.controllerToWrite.get(0).toString());
                            //controllerConnector.controllerToWrite.remove(0); controllerConnector.sendDataToWriteSent = 0;
                        }
                    }
                    ready2Write = false;    //
                    if (false) appendToLog("ready2Write is set false after true sendSiliconLabIcToWrite");
                } else if (bluetoothConnector.bluetoothIcToWrite.size() != 0) {
                    appendToLog("AAA 6");
                    if (isConnected() == false) bluetoothConnector.bluetoothIcToWrite.clear();
                    else if (System.currentTimeMillis() - btSendTime > btSendTimeOut) {
                        byte[] dataOut = bluetoothConnector.sendBluetoothIcToWrite(false);
                        boolean retValue = false;

                        if (utility.DEBUG_PKDATA && bluetoothConnector.sendDataToWriteSent != 0)
                            appendToLog("!!! mBluetoothIcDevice.sendDataToWriteSent = " + bluetoothConnector.sendDataToWriteSent);
                        if (utility.DEBUG_PKDATA)
                            appendToLog(String.format("PkData: write mBluetoothIcDevice.%s.%s with mBluetoothIcDevice.sendDataToWriteSent = %d",
                                    bluetoothConnector.bluetoothIcToWrite.get(0).bluetoothIcPayloadEvent.toString(),
                                    byteArrayToString(bluetoothConnector.bluetoothIcToWrite.get(0).dataValues),
                                    bluetoothConnector.sendDataToWriteSent));
                        if (bluetoothConnector.sendDataToWriteSent != 0)
                            appendToLog("!!! mBluetoothIcDevice.sendDataToWriteSent = " + bluetoothConnector.sendDataToWriteSent);

                        if (dataOut != null) {
                            appendToLog("CsReaderConnector.mReadWriteRunnable.run: 5 going to writeData with dataOut = " + byteArrayToString(dataOut));
                            retValue = writeData(dataOut, 0);
                        }
                        if (retValue) {
                            //bluetoothConnector.sendDataToWriteSent++;
                        } else {
                            //if (DEBUG) appendToLogView("failure to send " + bluetoothConnector.bluetoothIcToWrite.get(0).bluetoothIcPayloadEvent.toString());
                            //bluetoothConnector.bluetoothIcToWrite.remove(0); bluetoothConnector.sendDataToWriteSent = 0;
                        }
                    }
                    ready2Write = false;
                    appendToLog("ready2Write is set false after non-zero mBluetoothIcToWrite.size()");
                } else if (barcodeConnector.barcodeToWrite.size() != 0 && barcodeConnector.barcodePowerOnTimeOut == 0) {
                    appendToLog("AAA 7 barcodeToWrite.size = " + barcodeConnector.barcodeToWrite.size());
                    if (isConnected() == false) { barcodeConnector.barcodeToWrite.clear(); appendToLog("barcodeToWrite is clear"); }
                    else if (System.currentTimeMillis() - btSendTime > btSendTimeOut) {
                        byte[] dataOut = barcodeConnector.sendBarcodeToWrite( false);
                        if (dataOut != null) {
                            appendToLog("CsReaderConnector.mReadWriteRunnable.run: BtDataOut, 6 going to writeData with dataOut = " + byteArrayToString(dataOut));
                            writeData(dataOut, (barcodeConnector.barcodeToWrite.get(0).waitUplinkResponse ? 500 : 0));
                        }
                    }
                    ready2Write = false;
                    appendToLog("ready2Write is set false after true sendBarcodeToWrite");
                } else if (rfidConnector.rfidFailure == false && rfidConnector.rfidToWrite.size() != 0 && rfidConnector.rfidPowerOnTimeOut == 0) {
                    appendToLog("aabb 3");
                    if (utility.DEBUG_BTDATA) appendToLog("CsReaderConnector.mReadWriteRunnable rfidFailure is false and mRfidToWrite.size is " + rfidConnector.rfidToWrite.size());
                    if (isConnected() == false) {
                        rfidConnector.rfidToWrite.clear(); appendToLog("BtDataOut: mRfidToWrite.clear 5");
                    } else {
                        if (utility.DEBUG_BTDATA)
                            appendToLog("CsReaderConnector.mReadWriteRunnable 3 currentTime = " + System.currentTimeMillis() + ", btSendTime = " + btSendTime + ", difference = " + (System.currentTimeMillis() - btSendTime) + ", btSendTimeOut = " + btSendTimeOut);
                        appendToLog("aabb 4");
                        if (System.currentTimeMillis() - btSendTime > btSendTimeOut) {
                            boolean retValue = false;
                            appendToLog("aabb 5c");
                            byte[] dataOut = rfidConnector.sendRfidToWrite(false);
                            if (dataOut != null) {
                                appendToLog("CsReaderConnector.mReadWriteRunnable.run: 7 going to writeData with dataOut = " + byteArrayToString(dataOut));
                                retValue = writeData(dataOut, (rfidConnector.rfidToWrite.get(0).waitUplinkResponse ? 500 : 0));
                                if (false) appendToLog("done writeData with waitUplinkResponse = " + rfidConnector.rfidToWrite.get(0).waitUplinkResponse);

                                if (false) appendToLog("AAA sending rifd data = " + byteArrayToString(dataOut));
                                String string = byteArrayToString(dataOut).substring(16);
                                String stringCompare = "800280B310A";
                                if (bis108) stringCompare = "8002700100F00F000000";
                                if (false) appendToLog("AAA sending rifd data portion = " + string + ", " + string.indexOf(stringCompare));
                            }
                            if (DEBUG && !rfidConnector.rfidToWrite.isEmpty())
                                appendToLog("BtDataOut: done writeRfid with size = " + rfidConnector.rfidToWrite.size() + ", PayloadEvents = " + rfidConnector.rfidToWrite.get(0).rfidPayloadEvent.toString() + ", data=" + byteArrayToString(rfidConnector.rfidToWrite.get(0).dataValues));
                            rfidConnector.sendRfidToWriteSent++;
                            if (retValue) {
                                rfidConnector.mRfidToWriteRemoved = false;
                                if (DEBUG)
                                    appendToLog("writeRfid() with sendRfidToWriteSent = " + rfidConnector.sendRfidToWriteSent);
                                sendFailure = false;
                                //bValue = true;
                            } else sendFailure = true;

                            if (retValue) {
                                if (false) appendToLog("ready2Write is set false after true sendRfidToWrite");
                                ready2Write = false;
                            }
                        }
                    }
                }
            }
            /*if (validBuffer) {
                if (DEBUG)  appendToLog("mReadWriteRunnable: END, timer2Write=" + timer2Write + ", streamInBufferSize = " + bluetoothGatt.getStreamInBufferSize() + ", mRfidToRead.size=" + rfidConnector.mRfidToRead.size() + ", mRx000ToRead.size=" + rfidReader.mRx000ToRead.size());
            }*/
            //appendToLog("mRfidDevice is " + (mRfidDevice == null ? "null" : "valid"));
            //appendToLog("mRfidDevice.mRfidReaderChip is " + (mRfidDevice.mRfidReaderChip == null ? "null" : "valid"));
            //appendToLog("mRfidDevice.mRfidReaderChip.mRfidReaderChip is " + (mRfidDevice.mRfidReaderChip.mRfidReaderChip == null ? "null" : "valid"));
            if (rfidReader != null) rfidReader.uplinkHandler(invalidata);
            if (DEBUGTHREAD) appendToLog("CsReaderConnector.mReadWriteRunnable.run: ends");
        }
    };

    int intervalRx000UplinkHandler = 250;
    /*private final Runnable runnableRx000UplinkHandler = new Runnable() {
        @Override
        public void run() {
//            mRfidDevice.mRx000Device.mRx000UplinkHandler();
            mHandler.postDelayed(runnableRx000UplinkHandler, intervalRx000UplinkHandler);
        }
    };
    */

    public String getModelName() {
        boolean DEBUG = true;
        if (bis108) return controllerConnector.getModelName();
        String strModelName = controllerConnector.getModelName();
        if (DEBUG) appendToLog("Cs710Library4A.getModelName 0xb006 = " + strModelName);
        if (true) {
            String strModelName1 = rfidReader.rfidReaderChipE710.rx000Setting.getModelCode();
            if (DEBUG) appendToLog("Cs710Library4A.getModelName strModelName1 = " + strModelName1);
            if (true || strModelName == null || strModelName.length() == 0) {
                if (DEBUG) appendToLog("Cs710Library4A.getModelName strModeName is updated as modeCode");
                strModelName = strModelName1;
            }
        }
        return strModelName;
    }

    public int getBatteryLevel() {
        int iValue = csConnectorData.getVoltageMv();
        if (!bis108) {
            String hostVersion = controllerConnector.getVersion(); //false if lower, true if equal or higher
            String strVersionHost = "2.1.5";
            String[] strHostVersions = strVersionHost.split("\\.");
            boolean bResult = utility.checkHostProcessorVersion(hostVersion, Integer.parseInt(strHostVersions[0].trim()), Integer.parseInt(strHostVersions[1].trim()), Integer.parseInt(strHostVersions[2].trim()));
            if (false)
                appendToLog("getBatteryLevel: hostVersion = " + hostVersion + ", bResult = " + bResult + ", level = " + iValue);
            if (!bResult) {
                if (iValue >= 4450) iValue -= 430;
                else if (iValue > 350) iValue -= 350;
            }
        }
        return iValue;
    }

    public String checkVersion() {
        appendToLog("CsReaderConnector.checkVersion: starts");
        String macVersion = rfidReader.getMacVer();
        String hostVersion = controllerConnector.getVersion();
        String bluetoothVersion = bluetoothConnector.getBluetoothIcVersion();
        String stringPopup = "";
        int icsModel = bluetoothConnector.getCsModel();

        if (!rfidReader.isRfidFailure()) {
            if (bis108) return null; //Assume no checking
            if (bis108) {
                String strVersionRFID = "2.6.46"; String[] strRFIDVersions = strVersionRFID.split("\\.");
                String strVersionBT = "1.0.22"; String[] strBTVersions = strVersionBT.split("\\.");
                String strVersionHost = "1.0.17"; String[] strHostVersions = strVersionHost.split("\\.");
                if (true) {
                    if (utility.checkHostProcessorVersion(macVersion, Integer.parseInt(strRFIDVersions[0].trim()), Integer.parseInt(strRFIDVersions[1].trim()), Integer.parseInt(strRFIDVersions[2].trim())) == false)
                        stringPopup += "\nRFID processor firmware: V" + strVersionRFID;
                    if (icsModel != 463) {
                        if (utility.checkHostProcessorVersion(hostVersion, Integer.parseInt(strHostVersions[0].trim()), Integer.parseInt(strHostVersions[1].trim()), Integer.parseInt(strHostVersions[2].trim())) == false)
                            stringPopup += "\nSiliconLab firmware: V" + strVersionHost;
                        if (utility.checkHostProcessorVersion(bluetoothVersion, Integer.parseInt(strBTVersions[0].trim()), Integer.parseInt(strBTVersions[1].trim()), Integer.parseInt(strBTVersions[2].trim())) == false)
                            stringPopup += "\nBluetooth firmware: V" + strVersionBT;
                    }
                }
            } else {
                String strVersionRFID = "";
                String strVersionHost = "";
                String strVersionBT = "1.0.14";
                boolean bValidMac = true;
                if (macVersion == null) {
                    bValidMac = false;
                    stringPopup += "Null RFID firmware version";
                } else if (macVersion.indexOf("2.02") == 0) {
                    strVersionRFID = "2.2.2";
                    strVersionHost = "2.2.2";
                } else if (macVersion.indexOf("2.01") == 0) {
                    strVersionRFID = "2.1.2";
                    strVersionHost = "2.1.21";
                } else if (macVersion.indexOf("2.00") == 0) {
                    strVersionRFID = "2.0.0";
                    strVersionHost = "2.0.7";
                } else if (macVersion.indexOf("1.2.") == 0) {
                    strVersionRFID = "1.2.0";
                    strVersionHost = "0.2.20";
                } else {
                    bValidMac = false;
                    stringPopup += ("Unknown RFID firmware version " + macVersion);
                }
                String[] strRFIDVersions = strVersionRFID.split("\\.");
                String[] strHostVersions = strVersionHost.split("\\.");
                String[] strBTVersions = strVersionBT.split("\\.");

                appendToLog("CsReaderConnector.checkVersion: macVersion is " + macVersion + ", hostVersion is " + hostVersion + ", bValidMac is " + bValidMac);
                if (true) {
                    strRFIDVersions = macVersion.split("\\.");
                    strHostVersions = hostVersion.split("\\.");
                    int iRfidVersion0 = Integer.parseInt(strRFIDVersions[0].trim());
                    int iRfidVersion1 = Integer.parseInt(strRFIDVersions[1].trim());
                    int iHostVersion0 = Integer.parseInt(strHostVersions[0].trim());
                    int iHostVersion1 = Integer.parseInt(strHostVersions[1].trim());
                    appendToLog("CsReaderConnector.checkVersion: iRfidVersion0 = " + iRfidVersion0 + ", iHostVersion0 = " + iHostVersion0 + ", iRfidVersion1 = " + iRfidVersion1 + ", iHostVersion1 = " + iHostVersion1);
                    if (iRfidVersion0 != iHostVersion0 ||  iRfidVersion1 != iHostVersion1) {
                        stringPopup = "RFID firmware and Atmel firmware mismatch. Please make sure they are compatible.";
                        appendToLog("CsReaderConnector.checkVersion: mismatched");
                    } else appendToLog("CsReaderConnector.checkVersion: Matched");
                } else if (bValidMac) {
                    appendToLog("CsReaderConnector.checkVersion, x: strVersionRFID is " + strVersionRFID + ", macVersion = " + macVersion);
                    if (false && !utility.checkHostProcessorVersion(macVersion, Integer.parseInt(strRFIDVersions[0].trim()), Integer.parseInt(strRFIDVersions[1].trim()), Integer.parseInt(strRFIDVersions[2].trim())))
                        stringPopup += "\nRFID processor firmware: V" + strVersionRFID;

                    appendToLog("CsReaderConnector.checkVersion: strHostVersions is " + strVersionHost + ", hostVersion = " + hostVersion);
                    if (hostVersion.indexOf(strVersionHost.substring(0, 4)) != 0 ||
                            !utility.checkHostProcessorVersion(hostVersion, Integer.parseInt(strHostVersions[0].trim()), Integer.parseInt(strHostVersions[1].trim()), Integer.parseInt(strHostVersions[2].trim())))
                        stringPopup += "\nAtmel firmware: V" + strVersionHost;

                    if (icsModel != 203) {
                        appendToLog("CsReaderConnector.checkVersion, x: strVersionBT is " + strVersionBT + ", bluetoothVersion = " + bluetoothVersion);
                        if (false && !utility.checkHostProcessorVersion(bluetoothVersion, Integer.parseInt(strBTVersions[0].trim()), Integer.parseInt(strBTVersions[1].trim()), Integer.parseInt(strBTVersions[2].trim())))
                            stringPopup += "\nBluetooth firmware: V" + strVersionBT;
                    }
                }
            }
        }
        return stringPopup;
    }
    public String getMacAddress(ReaderDevice readerDevice0Connect) {
        String stringMacAddress = null;
        if (true) appendToLog("CsReaderConnector.getMacAddress: starts with bluetoothGatt is " + (bluetoothGatt == null ? "null" : ("valid with connectionState = " + bluetoothGatt.bluetoothConnectionState)));
        if (bluetoothGatt != null && bluetoothGatt.bluetoothConnectionState != 0) {
            appendToLog("CsReaderConnector.getMacAddress: starts with bluetoothGatt.getmBluetoothDevice is " + (bluetoothGatt.getBluetoothGattDeviceConnected() == null ? "null" : "valid"));
            if (bluetoothGatt.getBluetoothGattDeviceConnected() != null) stringMacAddress = bluetoothGatt.getBluetoothGattDeviceConnected().getAddress();
        }
        if (stringMacAddress == null) {
            stringMacAddress = readerDevice0Connect.getAddress();
        }
        appendToLog("CsReaderConnector.getMacAddress: stringMacAddress = " + stringMacAddress);
        return stringMacAddress;
    }
    public ReaderDevice readerDevice0Connect;
    public final Runnable reinitaliseDataRunnable = new Runnable() {
        @Override
        public void run() {
            appendToLog("reset before: reinitaliseDataRunnable starts with inventoring=" + rfidReader.isInventoring() + ", mrfidToWriteSize=" + rfidReader.rfidToWriteSize());
            if (rfidReader.isInventoring() || rfidReader.rfidToWriteSize() != 0) {
                mHandler.removeCallbacks(reinitaliseDataRunnable);
                mHandler.postDelayed(reinitaliseDataRunnable, 500);
            } else {
                if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.reinitaliseDataRunnable Start checkVersionRunnable");
                mHandler.postDelayed(checkVersionRunnable, 500);
            }
        }
    };
    public Runnable checkVersionRunnable = new Runnable() {
        boolean DEBUG = false;
        @Override
        public void run() {
            if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.checkVersionRunnable with mrfidToWriteSize = " + rfidReader.rfidToWriteSize());
            if (rfidReader == null || barcodeNewland == null || rfidReader.mRfidToWrite.size() != 0) {
                mHandler.removeCallbacks(checkVersionRunnable);
                mHandler.postDelayed(checkVersionRunnable, 500);
            } else {
                if (bis108) {
                    //setSameCheck(false);
                    rfidReader.setAccessCount(0); //appendToLog("btDataOut: setAccessCount as 0");
                    notificationConnector.setVersion(controllerConnector.getVersion());
                } else setSameCheck(false);
                if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.checkVersionRunnable with BarcodeFailure = " + barcodeConnector.isBarcodeFailure()); ///
                if (false && barcodeConnector.isBarcodeFailure() == false) {
                    if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.checkVersionRunnable"); ///5
                    if (barcodeNewland.checkPreSuffix(barcodeNewland.prefixRef, barcodeNewland.suffixRef) == false) barcodeNewland.barcodeSendCommandSetPreSuffix();
                    if (barcodeNewland.bBarcodeTriggerMode != 0x30) barcodeNewland.barcodeSendCommandTrigger();
                    notificationConnector.getAutoRFIDAbort(); notificationConnector.getAutoBarStartSTop(); //setAutoRFIDAbort(false); setAutoBarStartSTop(true);
                }
                rfidReader.setAntennaCycle(0xffff);
                if (bis108) {
                    if (bluetoothConnector.getCsModel() == 463) {
                        if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.checkVersionRunnable 4");
                        rfidReader.setAntennaDwell(2000);
                        rfidReader.setAntennaInvCount(0);
                    } else {
                        if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.checkVersionRunnable 5");
                        rfidReader.setAntennaDwell(0);
                        rfidReader.setAntennaInvCount(0xfffffffeL);
                    }
                }
                if (bluetoothConnector.getCsModel() == 203) {
                    appendToLog("Cs710Library4A.checkVersionRunnable. csModel = 203");
                    rfidReader.setAntennaSelect(1); rfidReader.setAntennaEnable(true);
                    rfidReader.setAntennaSelect(0); rfidReader.setAntennaEnable(false);
                }
                settingData.loadWedgeSettingFile();
                if (false) appendToLog("Cs710Library4A.checkVersionRunnable.run: going to loadSetting1File");
                if (loadSetting1File(readerDevice0Connect)) loadSetting1File(readerDevice0Connect);
                appendToLog("Cs710library4A.checkVersionRunnable.run: going to removeBond"); bluetoothGatt.removeBond(null, settingData.getPartnerReaderName());
                appendToLog("Cs710Library4A.checkVersionRunnable, getMacVer");
                if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.checkVersionRunnable with macVersion = " + rfidReader.getMacVer());
                if (bis108) {
                    if (utility.checkHostProcessorVersion(rfidReader.getMacVer(), 2, 6, 8)) {
                        rfidReader.setTagDelay(rfidReader.tagDelaySetting);
                        rfidReader.setCycleDelay(rfidReader.cycleDelaySetting);
                        rfidReader.setInvModeCompact(true);
                    } else {
                        rfidReader.setTagDelay(rfidReader.tagDelayDefaultNormalSetting);
                        rfidReader.setCycleDelay(rfidReader.cycleDelaySetting);
                    }
                    rfidReader.setDiagnosticConfiguration(true);
                } else if (true) {
                    rfidReader.setTagDelay(rfidReader.tagDelaySetting);
                    rfidReader.setCycleDelay(rfidReader.cycleDelaySetting);
                    rfidReader.setInvModeCompact(true);
                } else {
                    rfidReader.setTagDelay(rfidReader.tagDelayDefaultNormalSetting);
                    rfidReader.setCycleDelay(rfidReader.cycleDelaySetting);
                }
                if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.checkVersionRunnable ends with mRfidToWrite.size = " + rfidReader.mRfidToWrite.size());
                setSameCheck(true);
            }
        }
    };
    public boolean loadSetting1File(ReaderDevice readerDevice0Connect) {
        String strLibraryVersion = utility.StringVersionHeader + getlibraryVersion();
        return settingData.loadSettingFile(bluetoothConnector, getMacAddress(readerDevice0Connect), strLibraryVersion, rfidReader.getChannelHoppingStatus(), rfidReader.getCurrentProfile());
    }
    public byte[] onNotificationEvent() {
        byte[] notificationData = null;
        if (notificationConnector == null) {
            appendToLog("notificationConnector is null");
            return null;
        }
        if (notificationConnector.notificationToRead.size() != 0) {
            NotificationConnector.CsReaderNotificationData csReaderNotificationData = notificationConnector.notificationToRead.get(0);
            notificationConnector.notificationToRead.remove(0);
            if (csReaderNotificationData != null) notificationData = csReaderNotificationData.dataValues;
        }
        return notificationData;
    }
    public String getHostProcessorICBoardVersion() {
        String str;
        if (bluetoothConnector.getCsModel() == 463 || bluetoothConnector.getCsModel() == 203) str = rfidReader.getProductSerialNumber();
        else str = controllerConnector.getSerialNumber();
        if (str != null) {
            if (bis108) {
                if (str.length() == 16) {
                    String string = "";
                    if (str.substring(13, 14).matches("0") == false) string = str.substring(13, 14);
                    string += (string.length() != 0 ? "." : "") + str.substring(14, 15);
                    if (str.substring(15, 16).matches("0") == false || string.length() < 3)
                        string += (string.length() < 3 ? "." : "") + str.substring(15, 16);
                    str = string;
                }
            } else {
                if (str.length() < 16+4) return null;
                str = str.substring(16);
                String string = "";
                if (str.length() >= 1) string = str.substring(0,1);
                if (str.length() >= 3) string += ("." + str.substring(1, 3));
                if (str.length() >= 4) string += ("." + str.substring(3, 4));
                if (false) {
                    if (str.length() >= 5) string += (", " + str.substring(4, 5));
                    if (str.length() >= 7) string += ("." + str.substring(5, 7));
                    if (str.length() >= 8) string += ("." + str.substring(7, 8));
                }
                str = string;
            }
        }
        return str;
    }
    public String strVersionMBoard = "1.8"; public String[] strMBoardVersions = strVersionMBoard.split("\\.");
    public int iBatteryCount;
    public int iBatteryNewCurveDelay; public boolean bUsingInventoryBatteryCurve = false; public float fBatteryValueOld; public int iBatteryPercentOld;
    public int getBatteryValue2Percent(float floatValue) {
        boolean DEBUG = false;
        if (DEBUG) appendToLog("getHostProcessorICBoardVersion = " + getHostProcessorICBoardVersion() + ", strVersionMBoard = " + strVersionMBoard);
        if (false || utility.checkHostProcessorVersion(getHostProcessorICBoardVersion(), Integer.parseInt(strMBoardVersions[0].trim()), Integer.parseInt(strMBoardVersions[1].trim()), 0)) {
            final float[] fValueStbyRef = {
                    (float) 4.212, (float) 4.175, (float) 4.154, (float) 4.133, (float) 4.112,
                    (float) 4.085, (float) 4.069, (float) 4.054, (float) 4.032, (float) 4.011,
                    (float) 3.990, (float) 3.969, (float) 3.953, (float) 3.937, (float) 3.922,
                    (float) 3.901, (float) 3.885, (float) 3.869, (float) 3.853, (float) 3.837,
                    (float) 3.821, (float) 3.806, (float) 3.790, (float) 3.774, (float) 3.769,
                    (float) 3.763, (float) 3.758, (float) 3.753, (float) 3.747, (float) 3.742,
                    (float) 3.732, (float) 3.721, (float) 3.705, (float) 3.684, (float) 3.668,
                    (float) 3.652, (float) 3.642, (float) 3.626, (float) 3.615, (float) 3.605,
                    (float) 3.594, (float) 3.584, (float) 3.568, (float) 3.557, (float) 3.542,
                    (float) 3.531, (float) 3.510, (float) 3.494, (float) 3.473, (float) 3.457,
                    (float) 3.436, (float) 3.410, (float) 3.362, (float) 3.235, (float) 2.987,
                    (float) 2.982
            };
            final float[] fPercentStbyRef = {
                    (float) 100, (float) 98, (float) 96, (float) 95, (float) 93,
                    (float)  91, (float) 89, (float) 87, (float) 85, (float) 84,
                    (float)  82, (float) 80, (float) 78, (float) 76, (float) 75,
                    (float)  73, (float) 71, (float) 69, (float) 67, (float) 65,
                    (float)  64, (float) 62, (float) 60, (float) 58, (float) 56,
                    (float)  55, (float) 53, (float) 51, (float) 49, (float) 47,
                    (float)  45, (float) 44, (float) 42, (float) 40, (float) 38,
                    (float)  36, (float) 35, (float) 33, (float) 31, (float) 29,
                    (float)  27, (float) 25, (float) 24, (float) 22, (float) 20,
                    (float)  18, (float) 16, (float) 15, (float) 13, (float) 11,
                    (float)   9, (float)  7, (float)  5, (float)  4, (float)  2,
                    (float)   0
            };
            final float[] fValueRunRef = {
                    (float) 4.106, (float) 4.017, (float) 3.98 , (float) 3.937, (float) 3.895,
                    (float) 3.853, (float) 3.816, (float) 3.779, (float) 3.742, (float) 3.711,
                    (float) 3.679, (float) 3.658, (float) 3.637, (float) 3.626, (float) 3.61 ,
                    (float) 3.584, (float) 3.547, (float) 3.515, (float) 3.484, (float) 3.457,
                    (float) 3.431, (float) 3.399, (float) 3.362, (float) 3.32 , (float) 3.251,
                    (float) 3.135
            };
            final float[] fPercentRunRef = {
                    (float) 100, (float) 96, (float) 92, (float) 88, (float) 84,
                    (float) 80,  (float) 76, (float) 72, (float) 67, (float) 63,
                    (float) 59,  (float) 55, (float) 51, (float) 47, (float) 43,
                    (float) 39,  (float) 35, (float) 31, (float) 27, (float) 23,
                    (float) 19,  (float) 15, (float) 11,  (float) 7, (float)  2,
                    (float) 0
            };
            float[] fValueRef = fValueStbyRef;
            float[] fPercentRef = fPercentStbyRef;

            if (true && iBatteryCount != csConnectorData.getVoltageCount()) {
                iBatteryCount = csConnectorData.getVoltageCount();
                iBatteryNewCurveDelay++;
            }
            if (rfidReader.mRfidToWrite.size() != 0) iBatteryNewCurveDelay = 0;
            else if (rfidReader.isInventoring()) {
                if (bUsingInventoryBatteryCurve == false) { if (iBatteryNewCurveDelay > 1) { iBatteryNewCurveDelay = 0; bUsingInventoryBatteryCurve = true; } }
                else iBatteryNewCurveDelay = 0;
            } else if (bUsingInventoryBatteryCurve) { if (iBatteryNewCurveDelay > 2) { iBatteryNewCurveDelay = 0; bUsingInventoryBatteryCurve = false; } }
            else iBatteryNewCurveDelay = 0;

            if (bUsingInventoryBatteryCurve) {
                fValueRef = fValueRunRef;
                fPercentRef = fPercentRunRef;
            }
            if (DEBUG) appendToLog("NEW Percentage cureve is USED with bUsingInventoryBatteryCurve = " + bUsingInventoryBatteryCurve + ", iBatteryNewCurveDelay = " + iBatteryNewCurveDelay);

            int index = 0;
            while (index < fValueRef.length) {
                if (floatValue > fValueRef[index]) break;
                index++;
            }
            if (DEBUG) appendToLog("Index = " + index);
            if (index == 0) return 100;
            if (index == fValueRef.length) return 0;
            float value = ((fValueRef[index - 1] - floatValue) / (fValueRef[index - 1] - fValueRef[index]));
            if (true) {
                value *= (fPercentRef[index -1] - fPercentRef[index]);
                value = fPercentRef[index - 1] - value;
            } else {
                value += (float) (index - 1);
                value /= (float) (fValueRef.length - 1);
                value *= 100;
                value = 100 - value;
            }
            value += 0.5;
            int iValue = (int) (value);
            if (iBatteryNewCurveDelay != 0) iValue = iBatteryPercentOld;
            else if (bUsingInventoryBatteryCurve && floatValue <= fBatteryValueOld && iValue >= iBatteryPercentOld) iValue = iBatteryPercentOld;
            fBatteryValueOld = floatValue; iBatteryPercentOld = iValue;
            return iValue;
        } else {
            if (DEBUG) appendToLog("OLD Percentage cureve is USED");
            if (floatValue >= 4) return 100;
            else if (floatValue < 3.4) return 0;
            else {
                float result = (float) 166.67 * floatValue - (float) 566.67;
                return (int) result;
            }
        }
    }
    public String isBatteryLow() {
        boolean batterylow = false;
        int iValue = getBatteryLevel();
        if (iValue == 0) return null;
        float fValue = (float) iValue / 1000;
        int iPercent = getBatteryValue2Percent(fValue);
        if (utility.checkHostProcessorVersion(getHostProcessorICBoardVersion(), Integer.parseInt(strMBoardVersions[0].trim()), Integer.parseInt(strMBoardVersions[1].trim()), 0)) {
            if (true) {
                if (rfidReader.isInventoring()) {
                    if (fValue < 3.520) batterylow = true;
                } else if (bUsingInventoryBatteryCurve == false) {
                    if (fValue < 3.626) batterylow = true;
                }
            } else if (iPercent <= 20) batterylow = true;
        } else if (true) {
            if (rfidReader.isInventoring()) {
                if (fValue < 3.45) batterylow = true;
            } else if (bUsingInventoryBatteryCurve == false) {
                if (fValue < 3.6) batterylow = true;
            }
        } else if (iPercent <= 8) batterylow = true;
        if (batterylow) return String.valueOf(iPercent);
        return null;
    }
    public String getBatteryDisplay(boolean voltageDisplay) {
        float floatValue = (float) getBatteryLevel() / 1000;
        if (floatValue == 0)    return " ";
        String retString = null;
        if (voltageDisplay || (settingData.batteryDisplaySelect == 0)) retString = String.format("%.3f V", floatValue);
        else retString = (String.format("%d", getBatteryValue2Percent(floatValue)) + "%");
        if (voltageDisplay == false) retString +=  String.format("\r\n P=%d", rfidReader.getPwrlevel());
        return retString;
    }
    public final int iNO_SUCH_SETTING = 10000;
    public short getTriggerReportingCount() {
        boolean bValue = false;
        if (bluetoothConnector.getCsModel() != 463 && bluetoothConnector.getCsModel() != 203) bValue = utility.checkHostProcessorVersion(controllerConnector.getVersion(),  1, 0, 16);
        if (bValue == false) return iNO_SUCH_SETTING; else
            return settingData.triggerReportingCountSetting;
    }
    public boolean batteryLevelRequest() {
        if (rfidReader == null) return false;
        if (notificationConnector == null) return false;
        if (rfidReader.isInventoring()) {
            appendToLog("Skip batteryLevelREquest as inventoring !!!");
            return true;
        }
        if (rfidReader.rfidToWriteSize() != 0) return false;
        return notificationConnector.batteryLevelRequest();
    }
    public String getHostProcessorICSerialNumber() {
        String str;
        if (bluetoothConnector.getCsModel() != 463 && bluetoothConnector.getCsModel() != 203) str = controllerConnector.getSerialNumber();
        else str = rfidReader.getProductSerialNumber();
        if (str != null) {
            if (bis108) {
                if (str.length() > 13) return str.substring(0, 13);
            } else if (str.length() >= 16) return str.substring(0, 16);
        }
        return null;
    }
    public boolean setCsvColumnSelectSetting(int csvColumnSelect) {
        settingData.csvColumnSelect = csvColumnSelect;
        return true;
    }
    public boolean setSavingFormatSetting(int savingFormatSelect) {
        if (false) appendToLog("savingFormatSelect = " + savingFormatSelect);
        if (savingFormatSelect < 0 || savingFormatSelect > 1)   return false;
        settingData.savingFormatSelect = savingFormatSelect;
        return true;
    }
    public boolean setVibrateModeSetting(int vibrateModeSelect) {
        if (vibrateModeSelect < 0 || vibrateModeSelect > 1)   return false;
        settingData.vibrateModeSelect = vibrateModeSelect;
        return true;
    }
    public boolean setPartnerReaderName(String partnerReaderName) {
        partnerReaderName = partnerReaderName;
        return true;
    }
    public boolean setServerImpinjPassword(String serverImpinjPassword) {
        settingData.serverImpinjPassword = serverImpinjPassword;
        return true;
    }
    public boolean setServerImpinjName(String serverImpinjName) {
        settingData.serverImpinjName = serverImpinjName;
        return true;
    }
    public byte[] barcodeDataStore = null; long timeBarcodeData;
    public byte[] onBarcodeEvent() {
        byte[] barcodeData = null;
        if (barcodeConnector.mBarcodeToRead.size() != 0) {
            BarcodeConnector.CsReaderBarcodeData csReaderBarcodeData = barcodeConnector.mBarcodeToRead.get(0);
            barcodeConnector.mBarcodeToRead.remove(0);
            if (csReaderBarcodeData != null) {
                if (csReaderBarcodeData.barcodePayloadEvent == BarcodeConnector.BarcodePayloadEvents.BARCODE_GOOD_READ) {
                    if (false) barcodeData = "<GR>".getBytes();
                } else if (csReaderBarcodeData.barcodePayloadEvent == BarcodeConnector.BarcodePayloadEvents.BARCODE_DATA_READ) {
                    barcodeData = csReaderBarcodeData.dataValues;
                }
            }
        }

        byte[] barcodeCombined = null;
        if (false) barcodeCombined = barcodeData;
        else if (barcodeData != null) {
            appendToLog("BarStream: barcodeData = " + byteArrayToString(barcodeData) + ", barcodeDataStore = " + byteArrayToString(barcodeDataStore));
            int barcodeDataStoreIndex = 0;
            int length = barcodeData.length;
            if (barcodeDataStore != null) {
                barcodeDataStoreIndex = barcodeDataStore.length;
                length += barcodeDataStoreIndex;
            }
            barcodeCombined = new byte[length];
            if (barcodeDataStore != null)
                System.arraycopy(barcodeDataStore, 0, barcodeCombined, 0, barcodeDataStore.length);
            System.arraycopy(barcodeData, 0, barcodeCombined, barcodeDataStoreIndex, barcodeData.length);
            barcodeDataStore = barcodeCombined;
            timeBarcodeData = System.currentTimeMillis();
            barcodeCombined = new byte[0];
        }
        if (barcodeDataStore != null) {
            barcodeCombined = new byte[barcodeDataStore.length];
            System.arraycopy(barcodeDataStore, 0, barcodeCombined, 0, barcodeCombined.length);

            if (System.currentTimeMillis() - timeBarcodeData < 300) barcodeCombined = null;
            else barcodeDataStore = null;
        }
        if (barcodeCombined != null && barcodeNewland.getPrefix() != null && barcodeNewland.getSuffix() != null) {
            if (barcodeCombined.length == 0) barcodeCombined = null;
            else {
                byte[] prefixExpected = barcodeNewland.getPrefix(); boolean prefixFound = false;
                byte[] suffixExpected = barcodeNewland.getSuffix(); boolean suffixFound = false;
                int codeTypeLength = 4;
                appendToLog("BarStream: barcodeCombined = " + byteArrayToString(barcodeCombined) + ", Expected Prefix = " + byteArrayToString(prefixExpected)  + ", Expected Suffix = " + byteArrayToString(suffixExpected));
                if (barcodeCombined.length > prefixExpected.length + suffixExpected.length + codeTypeLength) {
                    int i = 0;
                    for (; i <= barcodeCombined.length - prefixExpected.length - suffixExpected.length; i++) {
                        int j = 0;
                        for (; j < prefixExpected.length; j++) {
                            if (barcodeCombined[i+j] != prefixExpected[j]) break;
                        }
                        if (j == prefixExpected.length) { prefixFound = true; break; }
                    }
                    int k = i + prefixExpected.length;
                    for (; k <= barcodeCombined.length - suffixExpected.length; k++) {
                        int j = 0;
                        for (; j < suffixExpected.length; j++) {
                            if (barcodeCombined[k+j] != suffixExpected[j]) break;
                        }
                        if (j == suffixExpected.length) { suffixFound = true; break; }
                    }
                    appendToLog("BarStream: iPrefix = " + i + ", iSuffix = " + k + ", with prefixFound = " + prefixFound + ", suffixFound = " + suffixFound);
                    if (prefixFound && suffixFound) {
                        byte[] barcodeCombinedNew = new byte[k - i - prefixExpected.length - codeTypeLength];
                        System.arraycopy(barcodeCombined, i + prefixExpected.length + codeTypeLength, barcodeCombinedNew, 0, barcodeCombinedNew.length);
                        barcodeCombined = barcodeCombinedNew;
                        appendToLog("BarStream: barcodeCombinedNew = " + byteArrayToString(barcodeCombinedNew));

                        if (true) {
                            byte[] prefixExpected1 = {0x5B, 0x29, 0x3E, 0x1E};
                            prefixFound = false;
                            byte[] suffixExpected1 = {0x1E, 0x04};
                            suffixFound = false;
                            appendToLog("BarStream: barcodeCombined = " + byteArrayToString(barcodeCombined) + ", Expected Prefix = " + byteArrayToString(prefixExpected1) + ", Expected Suffix = " + byteArrayToString(suffixExpected1));
                            if (barcodeCombined.length > prefixExpected1.length + suffixExpected1.length) {
                                i = 0;
                                for (; i <= barcodeCombined.length - prefixExpected1.length - suffixExpected1.length; i++) {
                                    int j = 0;
                                    for (; j < prefixExpected1.length; j++) {
                                        if (barcodeCombined[i + j] != prefixExpected1[j]) break;
                                    }
                                    if (j == prefixExpected1.length) {
                                        prefixFound = true;
                                        break;
                                    }
                                }
                                k = i + prefixExpected1.length;
                                for (; k <= barcodeCombined.length - suffixExpected1.length; k++) {
                                    int j = 0;
                                    for (; j < suffixExpected1.length; j++) {
                                        if (barcodeCombined[k + j] != suffixExpected1[j]) break;
                                    }
                                    if (j == suffixExpected1.length) {
                                        suffixFound = true;
                                        break;
                                    }
                                }
                                appendToLog("BarStream: iPrefix = " + i + ", iSuffix = " + k + ", with prefixFound = " + prefixFound + ", suffixFound = " + suffixFound);
                                if (prefixFound && suffixFound) {
                                    barcodeCombinedNew = new byte[k - i - prefixExpected1.length];
                                    System.arraycopy(barcodeCombined, i + prefixExpected1.length, barcodeCombinedNew, 0, barcodeCombinedNew.length);
                                    barcodeCombined = barcodeCombinedNew;
                                    appendToLog("BarStream: barcodeCombinedNew = " + byteArrayToString(barcodeCombinedNew));
                                }
                            }
                        }
                    }
                } else barcodeCombined = null;
            }
        }
        return barcodeCombined;
    }
    public boolean setBarcodeOn(boolean on) {
        boolean retValue;
        BarcodeConnector.CsReaderBarcodeData csReaderBarcodeData = new BarcodeConnector.CsReaderBarcodeData();
        if (on) csReaderBarcodeData.barcodePayloadEvent = BarcodeConnector.BarcodePayloadEvents.BARCODE_POWER_ON;
        else    csReaderBarcodeData.barcodePayloadEvent = BarcodeConnector.BarcodePayloadEvents.BARCODE_POWER_OFF;
        csReaderBarcodeData.waitUplinkResponse = false;
        retValue = barcodeConnector.barcodeToWrite.add(csReaderBarcodeData); appendToLog("barcodeToWrite added with size = " + barcodeConnector.barcodeToWrite.size());
        boolean continuousAfterOn = false;
        if (retValue && on && continuousAfterOn) {
            if (utility.checkHostProcessorVersion(bluetoothConnector.getBluetoothIcVersion(), 1, 0, 2)) {
                if (DEBUG) appendToLog("to barcodeSendCommandConinuous()");
                retValue = barcodeNewland.barcodeSendCommandConinuous();
            } else retValue = false;
        }
        if (DEBUG) appendToLog("barcodeToWrite size = " + barcodeConnector.barcodeToWrite.size());
        return retValue;
    }
    int iModeSet = -1, iVibratieTimeSet = -1;
    public boolean setVibrateOn(int mode) {
        boolean retValue;
        if (true) appendToLog("setVibrateOn with mode = " + mode + ", and isInventoring = " + rfidReader.isInventoring());
        if (rfidReader.isInventoring()) return false;
        BarcodeConnector.CsReaderBarcodeData csReaderBarcodeData = new BarcodeConnector.CsReaderBarcodeData();
        if (mode > 0) csReaderBarcodeData.barcodePayloadEvent = BarcodeConnector.BarcodePayloadEvents.BARCODE_VIBRATE_ON;
        else    csReaderBarcodeData.barcodePayloadEvent = BarcodeConnector.BarcodePayloadEvents.BARCODE_VIBRATE_OFF;
        csReaderBarcodeData.waitUplinkResponse = false;
        if (iModeSet == mode && iVibratieTimeSet == settingData.vibrateTimeSetting) {
            appendToLog("writeBleStreamOut: A7B3: Skip saving vibration data");
            return true;
        }
        if (mode > 0) {
            byte[] barcodeCommandData = new byte[3];
            barcodeCommandData[0] = (byte) (mode - 1);
            barcodeCommandData[1] = (byte) (settingData.vibrateTimeSetting / 256);
            barcodeCommandData[2] = (byte) (settingData.vibrateTimeSetting % 256);
            csReaderBarcodeData.dataValues = barcodeCommandData;
        }
        retValue = barcodeConnector.barcodeToWrite.add(csReaderBarcodeData); appendToLog("barcodeToWrite added with size = " + barcodeConnector.barcodeToWrite.size());
        if (DEBUG) appendToLog("barcodeToWrite size = " + barcodeConnector.barcodeToWrite.size());
        if (retValue) {
            iModeSet = mode; iVibratieTimeSet = settingData.vibrateTimeSetting;
        }
        return retValue;
    }
    boolean barcodeAutoStarted = false;
    public boolean barcodeInventory(boolean start) {
        boolean result = true;
        appendToLog("TTestPoint 0: " + start);
        if (start) {
            barcodeConnector.mBarcodeToRead.clear(); barcodeDataStore = null;
            if (barcodeConnector.getOnStatus() == false) { result = setBarcodeOn(true); appendToLog("TTestPoint 1"); }
            if (settingData.barcode2TriggerMode && result) {
                if (notificationConnector.getTriggerStatus() && notificationConnector.getAutoBarStartSTop()) {  appendToLog("TTestPoint 2"); barcodeAutoStarted = true; result = true; }
                else {  appendToLog("TTestPoint 3"); result = barcodeNewland.barcodeSendCommand(new byte[]{0x1b, 0x33}); }
            } else  appendToLog("TTestPoint 4");
            appendToLog("TTestPoint 5");
        } else {
            if (settingData.barcode2TriggerMode == false) {  appendToLog("TTestPoint 6"); result = setBarcodeOn(false); }
            else if (barcodeConnector.getOnStatus() == false && result) {  appendToLog("TTestPoint 7"); result = setBarcodeOn(true); }
            appendToLog("barcode2TriggerMode = " + settingData.barcode2TriggerMode + ", result = " + result + ", barcodeAutoStarted = " + barcodeAutoStarted);
            if (settingData.barcode2TriggerMode && result) {
                if (barcodeAutoStarted && result) {  appendToLog("TTestPoint 8"); barcodeAutoStarted = false; result = true; }
                result = barcodeNewland.barcodeSendCommand(new byte[] { 0x1b, 0x30 });
            } else  appendToLog("TTestPoint 10");
        }
        return result;
    }
    boolean deviceConnection = false;
    public boolean isReaderConnected() {
        boolean DEBUG = false;
        boolean deviceConnectionNew = isConnected();
        if (DEBUG) appendToLog("CsReaderConnector.isBleConnected: deviceConnectionNew = " + deviceConnectionNew + ", deviceConnection = " + deviceConnection);
        if (deviceConnectionNew) {
            if (deviceConnection == false) {
                deviceConnection = deviceConnectionNew;
                if (DEBUG || DEBUG_CONNECT) appendToLog("Debug_Connect, CsReaderConnector.isBleConnected: Newly connected");

                csConnectorDataInit();
                barcodeNewland = barcodeNewland;
                barcodeConnector = barcodeConnector;
                notificationConnector = notificationConnector;
                controllerConnector = controllerConnector;
                bluetoothConnector = bluetoothConnector;

                rfidReader.turnOn(true);
                setBarcodeOn(true);
                controllerConnector.getVersion();
                bluetoothConnector.getBluetoothIcVersion();
                rfidReader.channelOrderType = -1;
                {
                    //    getBarcodePreSuffix();
                    //    getBarcodeReadingMode();
                    //    getBarcodeSerial();
                    //getBarcodeNoDuplicateReading();
                    //getBarcodeDelayTimeOfEachReading();
                    //getBarcodeEnable2dBarCodes();
                    //getBarcodePrefixOrder();
                    //getBarcodeVersion();
                    //barcodeSendCommandLoadUserDefault();
                    //barcodeSendQuerySystem();
                    //    barcodeNewland.barcodeSendCommandItf14Cksum();
                    notificationConnector.setBatteryAutoReport(true); //0xA003
                    notificationConnector.setAutoRFIDAbort(true); //0xA004
                }
                if (!bis108) rfidReader.abortOperation();
                //getHostProcessorICSerialNumber(); //0xb004 (but access Oem as bluetooth version is not got)
                rfidReader.getMacVer();
                if (false) { //following two instructions seems not used
                    int iValue = rfidReader.getDiagnosticConfiguration();
                    if (DEBUG) appendToLog("Cs108Library4A.isBleConnected: diagnostic data = " + iValue);
                    rfidReader.macWrite(0xC08, 0x100);
                }
                rfidReader.getReaderDefault();
                rfidReader.regionCode = null;
                rfidReader.getModelNumber(getModelName());
                rfidReader.getCountryCode();
                if (bis108) {
                    rfidReader.getFreqModifyCode();
                    rfidReader.getSpecialCountryVersion();
                    rfidReader.getFreqChannelConfig();
                }
                //getSerialNumber();
                rfidReader.getQueryTarget();
                rfidReader.getImpinjExtension();
                rfidReader.getInvAlgoInChip();
                if (DEBUG_CONNECT || DEBUG) appendToLog("Debug_Connect, Cs108Library4A.isBleConnected: Start checkVersionRunnable");
                mHandler.postDelayed(checkVersionRunnable, 500);

                if (settingData.strForegroundReader.trim().length() != 0) {
                    settingData.strForegroundReader = bluetoothGatt.getBluetoothGattDeviceConnected().getAddress();
                }
                settingData.saveForegroundSetting2File();
            } else if (rfidReader == null) {
                deviceConnection = false;
                appendToLog("Cs108Library4A.isBleConnnected: csReaderConnector.rfidReader is NULL");
            } else if (rfidReader.bFirmware_reset_before) {
                rfidReader.bFirmware_reset_before = false;
                mHandler.postDelayed(reinitaliseDataRunnable, 500);
            }
        } else if (deviceConnection) {
            barcodeNewland = null; barcodeConnector = null;
            notificationConnector = null;
            controllerConnector = null;
            bluetoothConnector = null;
            deviceConnection = deviceConnectionNew;
            if (DEBUG) appendToLog("Cs108Library4A.isBleConnnected: Newly disconnected");
        }
        return(deviceConnection);
    }
    public void restoreAfterTagSelect() {
        if (utility.DEBUG_SELECT) appendToLog("Cs710Library4A.restoreAfterTagSelect: Debug_Select, isBleConnected = " + isReaderConnected());
        if (!isReaderConnected()) return;
        rfidReader.setSelectCriteriaDisable(0); rfidReader.setSelectCriteriaDisable(1); rfidReader.setSelectCriteriaDisable(2);
        loadSetting1File(readerDevice0Connect);
        rfidReader.setAccessCount(0);
        rfidReader.setRx000AccessPassword("00000000");
        if (true || utility.checkHostProcessorVersion(rfidReader.getMacVer(), 2, 6, 8)) {
            rfidReader.setMatchRep(0);
            rfidReader.setTagDelay(rfidReader.tagDelaySetting);
            rfidReader.setCycleDelay(rfidReader.cycleDelaySetting);
            rfidReader.setInvModeCompact(true);
        }
        if (rfidReader.postMatchDataChanged) {
            rfidReader.postMatchDataChanged = false;
            rfidReader.setPostMatchCriteria(rfidReader.postMatchDataOld.enable, rfidReader.postMatchDataOld.target, rfidReader.postMatchDataOld.offset, rfidReader.postMatchDataOld.mask);
            appendToLog("PowerLevel");
            rfidReader.setPowerLevel(rfidReader.postMatchDataOld.pwrlevel);
            appendToLog("writeBleStreamOut: invAlgo = " + rfidReader.postMatchDataOld.invAlgo); rfidReader.setInvAlgo1(rfidReader.postMatchDataOld.invAlgo == 3);
            rfidReader.setQValue1(rfidReader.postMatchDataOld.qValue);
        }
    }
    public void setReaderDefault() {
        rfidReader.setReaderDefault();
        String string = bluetoothGatt.getBluetoothGattDeviceConnected().getAddress();
        string = string.replaceAll("[^a-zA-Z0-9]","");
        string = string.substring(string.length()-6, string.length());
        if (bis108) bluetoothConnector.setBluetoothIcName("CS108Reader" + string);
        else bluetoothConnector.setBluetoothIcName("CS710Sreader" + string);
        //getlibraryVersion()
        if (false) {
            rfidReader.setCountryInList(rfidReader.countryInListDefault);
            rfidReader.setChannel(0);

            //getAntennaPower(0)
            //getPopulation()
            //getQuerySession()
            //getQueryTarget()
            rfidReader.setTagFocus(false);
            rfidReader.setFastId(false);
            //getInvAlgo()
            //\\getRetryCount()
            //getCurrentProfile() + "\n"));
            //\\getRxGain() + "\n"));
        }
        //getBluetoothICFirmwareName() + "\n");
        rfidReader.setTagDelay(rfidReader.tagDelaySettingDefault);
        rfidReader.setCycleDelay((long)0);
        rfidReader.setIntraPkDelay((byte)4);
        rfidReader.setDupDelay((byte)0);

        settingData.setBatteryDisplaySetting(settingData.batteryDisplaySelectDefault);
        settingData.setRssiDisplaySetting(settingData.rssiDisplaySelectDefault);
        notificationConnector.setTriggerReporting(settingData.triggerReportingDefault);
        notificationConnector.setTriggerReportingCount(settingData.triggerReportingCountSettingDefault);
        settingData.inventoryBeep = settingData.inventoryBeepDefault;
        settingData.beepCountSetting = settingData.beepCountSettingDefault;
        settingData.inventoryVibrate = settingData.inventoryVibrateDefault;
        settingData.vibrateTimeSetting = settingData.vibrateTimeSettingDefault;
        setVibrateModeSetting(settingData.vibrateModeSelectDefault);
        settingData.vibrateWindowSetting = settingData.vibrateWindowSettingDefault;

        setSavingFormatSetting(settingData.savingFormatSelectDefault);
        setCsvColumnSelectSetting(settingData.csvColumnSelectDefault);
        settingData.saveFileEnable = settingData.saveFileEnableDefault;
        settingData.saveCloudEnable = settingData.saveCloudEnableDefault;
        settingData.saveNewCloudEnable = settingData.saveNewCloudEnableDefault;
        settingData.saveAllCloudEnable = settingData.saveAllCloudEnableDefault;
        settingData.serverLocation = settingData.serverLocationDefault;
        settingData.serverTimeout = settingData.serverTimeoutDefault;
        settingData.barcode2TriggerMode = settingData.barcode2TriggerModeDefault;

        settingData.userDebugEnable = settingData.userDebugEnableDefault;
        settingData.preFilterData = null;
    }
    public int dataToWriteSize() {
        int iSize = 0;
        iSize += rfidConnector.rfidToWrite.size();
        iSize += barcodeConnector.barcodeToWrite.size();
        iSize += notificationConnector.notificationToWrite.size();
        iSize += controllerConnector.controllerToWrite.size();
        iSize += bluetoothConnector.bluetoothIcToWrite.size();
        return iSize;
    }
    public int rfidToWriteSize() {
        if (isReaderConnected() == false) return -1;
        if (rfidReader == null) return -1;
        return rfidReader.rfidToWriteSize();
    }
    public boolean bNeedReconnect = false;
    public int iConnectStateTimer = 0;
    public boolean connect1(ReaderDevice readerDevice) {
        boolean DEBUG = true;
        if (DEBUG || DEBUG_CONNECT)
            appendToLog("Debug_Connect, Cs108Library4A.Connect1:  with " + (readerDevice == null ? "null" : "valid") + " readerDevice, " + (readerDevice0Connect == null ? "null" : "valid" + "readerDeviceConnect"));
        if (readerDevice == null && readerDevice0Connect != null) readerDevice = readerDevice0Connect;
        boolean result = false;
        if (readerDevice != null) {
            bNeedReconnect = false;
            iConnectStateTimer = 0;
            bluetoothGatt.bDiscoverStarted = false;
            bluetoothGatt.setServiceUUIDType(readerDevice.getServiceUUID2p1());
            appendToLog("Cs108Library4A.connect1 is going to connect");
            result = connect(readerDevice);
        }
        if (DEBUG || DEBUG_CONNECT) appendToLog("Debug_Connect, Cs108Library4A.connect1: Result = " + result);
        return result;
    }
    public final Runnable disconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (barcodeConnector == null) return;
            appendToLog("abcc disconnectRunnable with barcodeToWrite.size = " + barcodeConnector.barcodeToWrite.size());
            if (barcodeConnector.barcodeToWrite.size() != 0)
                mHandler.postDelayed(disconnectRunnable, 100);
            else {
                appendToLog("disconnect G");
                disconnect();
            }
        }
    };
    boolean toggledConnection = false;
    public Runnable runnableToggleConnection = new Runnable() {
        @Override
        public void run() {
            if (DEBUG) appendToLog("runnableToggleConnection(): toggledConnection = " + toggledConnection + ", isBleConnected() = " + isReaderConnected());
            if (isReaderConnected() == false)  toggledConnection = true;
            if (toggledConnection) {
                if (isReaderConnected() == false) {
                    if (connect1(null) == false) return;
                } else return;
            } else { appendToLog("disconnect H"); disconnect(); appendToLog("done"); }
            mHandler.postDelayed(runnableToggleConnection, 500);
        }
    };
    public final Runnable connectRunnable = new Runnable() {
        boolean DEBUG = true;

        @Override
        public void run() {
            if (DEBUG || DEBUG_CONNECT) {
                appendToLog("Debug_Connect: Cs710Library4A.connectRunnable: isBleScanning = " + bluetoothGatt.isScanning());
                appendToLog("Debug_Connect: Cs710Library4A.connectRunnable: bNeedReconnect = " + bNeedReconnect);
                appendToLog("Debug_Connect: Cs710Library4A.connectRunnable: bluetoothConnectionState = " + bluetoothGatt.bluetoothConnectionState);
            }
            if (bluetoothGatt.isScanning()) {
                if (DEBUG) appendToLog("Cs710Library4A.connectRunnable: still scanning. Stop scanning first");
                scanLeDevice(false);
            } else if (bNeedReconnect) {
                if (bluetoothGatt.bluetoothGatt != null) {
                    if (DEBUG)
                        appendToLog("Cs710Library4A.connectRunnable: mBluetoothGatt is null before connect. disconnect first");
                    disconnect();
                } else if (readerDevice0Connect == null) {
                    if (DEBUG)
                        appendToLog("Cs710Library4A.connectRunnable: exit with null readerDeviceConnect");
                    return;
                } else if (bluetoothGatt.bluetoothGatt == null) {
                    if (DEBUG || DEBUG_CONNECT)
                        appendToLog("Debug_Connect, Cs710Library4A.connectRunnable: connect1 starts");
                    connect1(null);
                    bNeedReconnect = false;
                }
            } else if (bluetoothGatt.bluetoothConnectionState == BluetoothProfile.STATE_DISCONNECTED) { //mReaderStreamOutCharacteristic valid around 1500ms
                iConnectStateTimer = 0;
                if (DEBUG)
                    appendToLog("Cs710Library4A.connectRunnable: disconnect as disconnected connectionState is received");
                bNeedReconnect = true;
                disconnect();
            } else if (bluetoothGatt.mReaderStreamOutCharacteristic == null) {
                if (DEBUG_CONNECT)
                    appendToLog("Debug_Connect, Cs710Library4A.connectRunnable: wait as not yet discovery, with iConnectStateTimer = " + iConnectStateTimer);
                if (++iConnectStateTimer > 10) {
                }
            } else {
                if (DEBUG_CONNECT) appendToLog("Debug_Connect, Cs710Library4A.connectRunnable: end of ConnectRunnable");
                return;
            }
            mHandler.postDelayed(connectRunnable, 500);
        }
    };
    public boolean scanLeDevice(boolean enable) {
        boolean DEBUG = true;
        if (enable) mHandler.removeCallbacks(connectRunnable);

        if (DEBUG || DEBUG_SCAN) appendToLog("Cs710Library4A.scanLeDevice[" + enable + "]");
        if (bluetoothGatt.bluetoothDeviceConnectOld != null) {
            if (DEBUG) appendToLog("Cs710Library4A.scanLeDevice: bluetoothDeviceConnectOld connection state is valid"); //= " + bluetoothGatt.bluetoothManager.getConnectionState(bluetoothGatt.bluetoothDeviceConnectOld, BluetoothProfile.GATT));
        }
        //if (enable && usbConnector != null) usbConnector.scanDevice(enable);
        boolean bValue = bluetoothGatt.scanDevice(enable, mLeScanCallback, mScanCallback);
        if (DEBUG || DEBUG_SCAN) appendToLog("Cs710Library4A.scanLeDevice: isScanning = " + bluetoothGatt.isScanning());
        return bValue;
    }
    public void disconnect(boolean tempDisconnect) {
        appendToLog("abcc tempDisconnect: getBarcodeOnStatus = " + (barcodeConnector.getOnStatus() ? "on" : "off"));
        if (DEBUG) appendToLog("tempDisconnect = " + tempDisconnect);
        mHandler.removeCallbacks(checkVersionRunnable);
        mHandler.removeCallbacks(runnableToggleConnection);
        if (barcodeConnector.getOnStatus()) {
            appendToLog("tempDisconnect: setBarcodeOn(false)");
            if (barcodeConnector.barcodeToWrite.size() != 0) {
                appendToLog("going to disconnectRunnable with remaining barcodeToWrite.size = " + barcodeConnector.barcodeToWrite.size() + ", data = " + byteArrayToString(barcodeConnector.barcodeToWrite.get(0).dataValues));
            }
            barcodeConnector.barcodeToWrite.clear(); appendToLog("barcodeToWrite is clear");
            setBarcodeOn(false);
            rfidReader.turnOn(false);
        } else appendToLog("tempDisconnect: getBarcodeOnStatus is false");
        mHandler.postDelayed(disconnectRunnable, 100);
        appendToLog("done with tempDisconnect = " + tempDisconnect);
        if (tempDisconnect == false)    {
            mHandler.removeCallbacks(connectRunnable);
            bluetoothGatt.bluetoothDeviceConnectOld = null;
            if (readerDevice0Connect != null) bluetoothGatt.bluetoothDeviceConnectOld = bluetoothGatt.bluetoothAdapter.getRemoteDevice(readerDevice0Connect.getAddress());
            readerDevice0Connect = null;
        }
    }
    public void connect2(ReaderDevice readerDevice) {
        if (readerDevice != null) {
            appendToLog("CsReaderConnector.connect2: going to removeBond"); bluetoothGatt.removeBond(readerDevice.getAddress(), null);
        }
        if (isReaderConnected()) return;
        if (bluetoothGatt.bluetoothGatt != null) disconnect();
        if (readerDevice != null) readerDevice0Connect = readerDevice;
        mHandler.removeCallbacks(connectRunnable);
        bNeedReconnect = true; mHandler.post(connectRunnable);
        if (DEBUG_CONNECT) appendToLog("Debug_Connect: Cs710Library4A.connect Start ConnectRunnable");
    }
}

