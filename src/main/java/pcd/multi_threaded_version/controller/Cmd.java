package pcd.multi_threaded_version.controller;

import pcd.multi_threaded_version.model.Board;

public interface Cmd {


    void execute(Board board);
}
