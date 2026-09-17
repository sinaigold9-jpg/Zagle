package com.zajel.app.domain;

public final class Attachment { public final String id, messageId, name, mimeType, storagePath; public final long sizeBytes; public Attachment(String i,String m,String n,String t,String p,long s){id=i;messageId=m;name=n;mimeType=t;storagePath=p;sizeBytes=s;} }
