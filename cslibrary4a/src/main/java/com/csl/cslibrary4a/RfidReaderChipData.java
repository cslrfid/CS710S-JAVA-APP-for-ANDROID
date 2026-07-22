package com.csl.cslibrary4a;

import android.util.Log;

public class RfidReaderChipData {
    public enum OperationTypes {
        TAG_RDOEM,
        TAG_INVENTORY_COMPACT, TAG_INVENTORY, TAG_SEARCHING
    }

    public enum HostCommands {
        NULL, CMD_WROEM, CMD_RDOEM, CMD_ENGTEST, CMD_MBPRDREG, CMD_MBPWRREG,
        CMD_18K6CINV, CMD_18K6CREAD, CMD_18K6CWRITE, CMD_18K6CLOCK, CMD_18K6CKILL, CMD_SETPWRMGMTCFG, CMD_18K6CAUTHENTICATE, CMD_UNTRACEABLE,
        CMD_UPDATELINKPROFILE,
        CMD_18K6CBLOCKWRITE,
        CMD_CHANGEEAS, CMD_GETSENSORDATA,
        CMD_READBUFFER,
        CMD_FDM_RDMEM, CMD_FDM_WRMEM, CMD_FDM_AUTH, CMD_FDM_GET_TEMPERATURE, CMD_FDM_START_LOGGING, CMD_FDM_STOP_LOGGING,
        CMD_FDM_WRREG, CMD_FDM_RDREG, CMD_FDM_DEEP_SLEEP, CMD_FDM_OPMODE_CHECK, CMD_FDM_INIT_REGFILE, CMD_FDM_LED_CTRL,
        CMD_18K6CINV_SELECT,
        CMD_18K6CINV_COMPACT, CMD_18K6CINV_COMPACT_SELECT,
        CMD_18K6CINV_MB, CMD_18K6CINV_MB_SELECT
    }

    public enum HostCmdResponseTypes {
        NULL,
        TYPE_COMMAND_BEGIN,
        TYPE_COMMAND_END,
        TYPE_18K6C_INVENTORY, TYPE_18K6C_INVENTORY_COMPACT,
        TYPE_18K6C_TAG_ACCESS,
        TYPE_ANTENNA_CYCLE_END,
        TYPE_COMMAND_ACTIVE,
        TYPE_COMMAND_ABORT_RETURN
    }

    public static class Rx000pkgData {
        public RfidReaderChipData.HostCmdResponseTypes responseType;
        public int flags;
        public byte[] dataValues;
        public long decodedTime;
        public double decodedRssi;
        public int decodedPhase, decodedChidx, decodedPort;
        public byte[] decodedPc, decodedEpc, decodedCrc, decodedData1, decodedData2;
        public String decodedResult;
        public String decodedError;

        public void getFrom0(RfidReaderChipData.Rx000pkgData rx000pkgData) {
            Log.i("Hello", "rx000pkgData0 is " + (rx000pkgData == null ? "null" : "valid"));
            if (rx000pkgData.responseType != null) responseType = HostCmdResponseTypes.values()[rx000pkgData.responseType.ordinal()];
            flags = rx000pkgData.flags;
            dataValues = rx000pkgData.dataValues;
            decodedTime = rx000pkgData.decodedTime;
            decodedRssi = rx000pkgData.decodedRssi;
            decodedPhase = rx000pkgData.decodedPhase;
            decodedChidx = rx000pkgData.decodedChidx;
            decodedPort = rx000pkgData.decodedPort;
            decodedPc = rx000pkgData.decodedPc;
            decodedEpc = rx000pkgData.decodedEpc;
            decodedCrc = rx000pkgData.decodedCrc;
            decodedData1 = rx000pkgData.decodedData1;
            decodedData2 = rx000pkgData.decodedData2;
            decodedResult = rx000pkgData.decodedResult;
            decodedError = rx000pkgData.decodedError;
        }
    }

    public enum CsvColumn {
        RESERVE_BANK,
        EPC_BANK,
        TID_BANK,
        USER_BANK,
        PHASE,
        CHANNEL,
        TIME, TIMEZONE,
        LOCATION, DIRECTION,
        OTHERS
    }
}
