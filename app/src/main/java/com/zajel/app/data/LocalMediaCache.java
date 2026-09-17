package com.zajel.app.data;

import android.content.*;import android.net.Uri;import java.io.*;
public final class LocalMediaCache { private final Context context; public LocalMediaCache(Context c){context=c.getApplicationContext();} public File copy(Uri uri,String name)throws Exception{String safe=name.replaceAll("[^A-Za-z0-9._-]","_");File out=File.createTempFile("zajel_", "_"+safe,context.getCacheDir());try(InputStream in=context.getContentResolver().openInputStream(uri);OutputStream o=new FileOutputStream(out)){if(in==null)throw new IOException("Unable to open selected file");byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)o.write(b,0,n);}return out;} public void delete(File file){if(file!=null)file.delete();} }
