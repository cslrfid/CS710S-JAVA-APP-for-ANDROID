package com.csl.cslibrary4a;

import android.util.Log;

public class ExtraBankData {
	public int extra1Bank;
	public int extra2Bank;
	public int extra1Count;
	public int extra2Count;
	public int extra1Offset;
	public int extra2Offset;
	public ExtraBankData() {
		extra1Bank = -1; extra2Bank = -1;
		extra1Count = 0; extra2Count = 0;
		extra1Offset = 0; extra2Offset = 0;
	}
	public void setExtraBankData(int extra1Bank, int extra1Count, int extra1Offset, int extra2Bank, int extra2Count, int extra2Offset) {
		this.extra1Bank = extra1Bank; this.extra2Bank = extra2Bank;
		this.extra1Count = extra1Count; this.extra2Count = extra2Count;
		this.extra1Offset = extra1Offset; this.extra2Offset = extra2Offset;
		Log.i("Hello", "RfidReader.setExtraBankData: DebugABC, Extra6, extra1Bank = " + extra1Bank + ", extra2Bank = " + extra2Bank);
	}
	public void setExtraBankData(RfidReaderData.TagType tagType, String mDid) {
		extra2Bank = 2;
		extra2Offset = 0;
		extra2Count = 2;
		Log.i("Hello", "RfidReader.setExtraBankData: DebugABC, tagType = " + (tagType == null ? "null" : tagType.toString()) + ", mDid = " + mDid);
		if (mDid == null) mDid = "";
		if (true && (tagType == RfidReaderData.TagType.TAG_IMPINJ_M775 || tagType == RfidReaderData.TagType.TAG_IMPINJ_M780 || tagType == RfidReaderData.TagType.TAG_IMPINJ_M830 || tagType == RfidReaderData.TagType.TAG_IMPINJ_M770 || tagType == RfidReaderData.TagType.TAG_IMPINJ_M730)) {
			extra1Bank = 0;
			extra1Offset = 4;
			extra1Count = 1;
			if (tagType == RfidReaderData.TagType.TAG_IMPINJ_M775) extra2Count = 6;
		} else if (tagType == RfidReaderData.TagType.TAG_EM_BAP /*mDid.matches("E200B0")*/) {
			extra1Bank = 2;
			extra1Offset = 0;
			extra1Count = 2;
			extra2Bank = 3;
			extra2Offset = 0x2d;
			extra2Count = 1;
		} else if (tagType == RfidReaderData.TagType.TAG_EM_COLDCHAIN /*mDid.indexOf("E280B0") == 0*/) {
			extra1Bank = 3;
			extra1Offset = 188;
			extra1Count = 2;
			//extra2Bank = 3;
			//extra2Offset = 0x10d;
			//extra2Count = 1;
		} else if (tagType == RfidReaderData.TagType.TAG_EM_AURASENSE || tagType == RfidReaderData.TagType.TAG_EM_AURASENSE_ATBOOT || tagType == RfidReaderData.TagType.TAG_EM_AURASENSE_ATSELECT /*mDid.indexOf("E280B12") == 0*/) {
			extra1Bank = 2;
			extra1Offset = 0;
			extra1Count = 2;
			extra2Bank = 3;
			extra2Offset = 0x120;
			extra2Count = 1;
		} else if (tagType == RfidReaderData.TagType.TAG_KILOWAY) { //mDid.indexOf("E281D") == 0) { //need atmel firmware 0.2.20
			extra1Bank = 0;
			extra1Offset = 4;
			extra1Count = 1;
			extra2Count = 6;
		} else if (tagType == RfidReaderData.TagType.TAG_LONGJING) { //mDid.indexOf("E201E") == 0) {
			extra1Bank = 3;
			extra1Offset = 112;
			extra1Count = 1;
			extra2Count = 6;
		} else if (tagType == RfidReaderData.TagType.TAG_MAGNUS_S2) { //mDid.matches("E282402")) {
			extra1Bank = 0;
			extra1Offset = 11;
			extra1Count = 1;
			extra2Bank = 0;
			extra2Offset = 13;
			extra2Count = 1;
		} else if (tagType == RfidReaderData.TagType.TAG_MAGNUS_S3) { //mDid.matches("E282403")) {
			extra1Bank = 0;
			extra1Offset = 12;
			extra1Count = 3;
			extra2Bank = 3;
			extra2Offset = 8;
			extra2Count = 4;
		} else if (tagType == RfidReaderData.TagType.TAG_AXZON_XERXES) { //mDid.matches("E282405")) {
			extra1Bank = 0;
			extra1Offset = 10;
			extra1Count = 5;
			extra2Bank = 3;
			extra2Offset = 0x12;
			extra2Count = 4;
		} else if (tagType == RfidReaderData.TagType.TAG_CTESIUS) { //mDid.matches("E203510")) {
			extra1Bank = 2;
			extra1Offset = 0;
			extra1Count = 2;
			extra2Bank = 3;
			extra2Offset = 8;
			extra2Count = 2;
		} else if (tagType == RfidReaderData.TagType.TAG_ASYGN) { //mDid.matches("E283A")) {
			extra1Bank = 2;
			extra1Offset = 0;
			extra1Count = 2;
			extra2Bank = 3;
			extra2Offset = 0;
			extra2Count = 8;
		}
		Log.i("Hello", "RfidReader.setExtraBankData: DebugABC, extra1Bank = " + extra1Bank + ", extra2Bank = " + extra2Bank);
	}
	public void adjustExtraBank1() {
		if (extra1Bank == -1 || extra1Count == 0) {
			extra1Bank = extra2Bank;
			extra2Bank = 0;
			extra1Count = extra2Count;
			extra2Count = 0;
			extra1Offset = extra2Offset;
			extra2Offset = 0;
		}
		if (extra1Bank == 1) extra1Offset += 2;
		if (extra2Bank == 1) extra2Offset += 2;
	}
}