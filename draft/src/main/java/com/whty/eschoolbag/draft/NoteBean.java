package com.whty.eschoolbag.draft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianqs on 2018/3/16.
 */

public class NoteBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private int index;
    private List<RobotPath> paths = new ArrayList<>();
    private List<RobotPath> recoverPaths = new ArrayList<>();


    public NoteBean(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<RobotPath> getPaths() {
        return paths;
    }

    public void setPaths(List<RobotPath> paths) {
        this.paths.clear();
        this.paths.addAll(paths);
    }

    public List<RobotPath> getRecoverPaths() {
        return recoverPaths;
    }

    public void setRecoverPaths(List<RobotPath> recoverPaths) {
        this.recoverPaths.clear();
        this.recoverPaths.addAll(recoverPaths);
    }
}
