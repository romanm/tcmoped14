package org.tasclin1.mopet.controller;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class MainTest {
    public static void main(String[] args) {
	System.out.println("-----");
	List<String> arrayList = new ArrayList<String>();
	arrayList.add("q");
	arrayList.add("w");
	boolean contains = arrayList.contains("q");
	System.out.println(contains);
	int indexOf = arrayList.indexOf("w");
	System.out.println(indexOf);
	// mtUUID();
    }

    private static void mtUUID() {
	getUid(22);
	getUid(23);
	System.out.println("-----");
	UUID randomUUID = UUID.randomUUID();
	System.out.println("-----" + randomUUID);
	UID uid = new UID();
	System.out.println("-----" + uid);
    }

    private static void getUid(int i) {
	long timeInMillis = Calendar.getInstance().getTimeInMillis();
	System.out.println("-----" + timeInMillis);
	UUID uuid = new UUID(i, timeInMillis);
	System.out.println("-----" + uuid);
	System.out.println("-----" + uuid.getMostSignificantBits());
	System.out.println("-----" + uuid.getLeastSignificantBits());
	System.out.println("-----" + uuid.version());
	System.out.println("-----" + uuid.variant());
	System.out.println("-----");
    }
}
