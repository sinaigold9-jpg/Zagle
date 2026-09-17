package com.zajel.app.domain;

public final class Message { public final String id,conversationId,senderId,body,createdAt; public final MessageStatus status; public final Attachment attachment; public Message(String i,String c,String s,String b,String t,MessageStatus st,Attachment a){id=i;conversationId=c;senderId=s;body=b;createdAt=t;status=st;attachment=a;} public Message(String i,String c,String s,String b,String t){this(i,c,s,b,t,MessageStatus.SENT,null);} }
