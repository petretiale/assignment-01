package pcd.executor_version.controller;

import pcd.executor_version.model.Board;

public interface Cmd {


    void execute(Board board);
}
