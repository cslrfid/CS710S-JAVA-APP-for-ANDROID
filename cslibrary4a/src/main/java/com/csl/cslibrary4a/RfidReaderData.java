package com.csl.cslibrary4a;

import android.util.Log;

import com.csl.cslibrary4a1.RfidReaderData0;

public class RfidReaderData {
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
        public RfidReaderData.HostCmdResponseTypes responseType;
        public int flags;
        public byte[] dataValues;
        public long decodedTime;
        public double decodedRssi;
        public int decodedPhase, decodedChidx, decodedPort;
        public byte[] decodedPc, decodedEpc, decodedCrc, decodedData1, decodedData2;
        public String decodedResult;
        public String decodedError;

        void getFrom0(RfidReaderData0.Rx000pkgData rx000pkgData0) {
            Log.i("Hello", "rx000pkgData0 is " + (rx000pkgData0 == null ? "null" : "valid"));
            if (rx000pkgData0.responseType != null) responseType = HostCmdResponseTypes.values()[rx000pkgData0.responseType.ordinal()];
            flags = rx000pkgData0.flags;
            dataValues = rx000pkgData0.dataValues;
            decodedTime = rx000pkgData0.decodedTime;
            decodedRssi = rx000pkgData0.decodedRssi;
            decodedPhase = rx000pkgData0.decodedPhase;
            decodedChidx = rx000pkgData0.decodedChidx;
            decodedPort = rx000pkgData0.decodedPort;
            decodedPc = rx000pkgData0.decodedPc;
            decodedEpc = rx000pkgData0.decodedEpc;
            decodedCrc = rx000pkgData0.decodedCrc;
            decodedData1 = rx000pkgData0.decodedData1;
            decodedData2 = rx000pkgData0.decodedData2;
            decodedResult = rx000pkgData0.decodedResult;
            decodedError = rx000pkgData0.decodedError;
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
	
	public enum TagType {
        TAG_NULL,
        TAG_IMPINJ,  //E28011
        TAG_IMPINJ_M775, TAG_IMPINJ_M780, TAG_IMPINJ_M830, TAG_IMPINJ_M770, TAG_IMPINJ_M730,  //E2C011(E2C011A2), E28011C, E28011B, E28011A, E280119
        TAG_IMPINJ_MONZA_R6A, TAG_IMPINJ_MONZA_R6P, TAG_IMPINJ_MONZA_R6, TAG_IMPINJ_MONZA_X8K,  //E2801171, E2801170, E2801160, E2801150
        TAG_IMPINJ_noUSER,  //E2001
        TAG_ALIEN,  //E2003
        TAG_NXP, TAG_NXP_UCODEDNA, TAG_NXP_UCODEDNA_AUTHMODE,  //E2806, E2C06, E2C06
        TAG_NXP_UCODE8, TAG_NXP_UCODE8_EPC, TAG_NXP_UCODE8_EPCTID, TAG_NXP_UCODE8_EPCBRAND, TAG_NXP_UCODE8_EPCBRANDTID,  //E2806894, E2806894A, E2806894B, E2806894C, E2806894d
        TAG_EM, TAG_EM_BAP, TAG_EM_COLDCHAIN, TAG_EM_AURASENSE, TAG_EM_AURASENSE_ATBOOT, TAG_EM_AURASENSE_ATSELECT,  //E280B, E200B0, E280B0, E280B12, E280B12A, E280B12B
        TAG_KILOWAY,  //E281D
        TAG_LONGJING,  //E201E
        TAG_AXZON, TAG_MAGNUS_S1, TAG_MAGNUS_S2, TAG_MAGNUS_S3, TAG_AXZON_XERXES, TAG_AXZON_OPUS,  //E2824, E282401, E282402, E282403, E282405, E2C24500

        TAG_FDMICRO,  //E2827001
        TAG_CTESIUS,  //E203510 -- not tested
        TAG_ASYGN,  //E283A -- not tested
    }
}
