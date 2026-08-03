package pcd.sketch01.controller;

import pcd.sketch01.model.Board;

public interface Cmd {


    void execute(Board board);
}
