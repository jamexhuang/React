package com.volmit.react.api;

import com.volmit.react.React;
import org.bukkit.entity.LivingEntity;
import primal.lang.collection.GMap;
import primal.lang.collection.GSet;

import java.io.*;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StackData {
    private GMap<UUID, Integer> emap;
    private GSet<UUID> touched;

    public StackData() {
        emap = new GMap<UUID, Integer>();
        touched = new GSet<UUID>();
    }

    public GMap<UUID, Integer> getEmap() {
        return emap;
    }

    public void setEmap(GMap<UUID, Integer> emap) {
        this.emap = emap;
    }

    public GSet<UUID> getTouched() {
        return touched;
    }

    public void setTouched(GSet<UUID> touched) {
        this.touched = touched;
    }

    public void clear() {
        for (UUID i : emap.k()) {
            if (!touched.contains(i)) {
                emap.remove(i);
            }
        }
    }

    public void save(File f) throws IOException {
        f.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(f);
        GZIPOutputStream gzo = new GZIPOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(gzo);
        dos.writeInt(emap.size());

        for (UUID i : emap.k()) {
            dos.writeLong(i.getMostSignificantBits());
            dos.writeLong(i.getLeastSignificantBits());
            dos.writeShort(emap.get(i));
        }

        dos.close();
    }

    public void load(File f) throws IOException {
        f.getParentFile().mkdirs();
        emap.clear();
        FileInputStream fin = new FileInputStream(f);
        GZIPInputStream gzi = new GZIPInputStream(fin);
        DataInputStream din = new DataInputStream(gzi);
        int s = din.readInt();

        for (int i = 0; i < s; i++) {
            emap.put(new UUID(din.readLong(), din.readLong()), (int) din.readShort());
        }

        din.close();
    }

    public void put(LivingEntity e) {
        if (React.instance.entityStackController.isStacked(e)) {
            StackedEntity se = React.instance.entityStackController.getStack(e);
            emap.put(e.getUniqueId(), se.getCount());
        } else {
            this.remove(e);
        }

        touched.add(e.getUniqueId());
    }

    public int get(LivingEntity e) {
        touched.add(e.getUniqueId());

        if (emap.containsKey(e.getUniqueId())) {
            return emap.get(e.getUniqueId());
        }

        return 0;
    }

    public void remove(LivingEntity entity) {
        emap.remove(entity.getUniqueId());
    }
}
