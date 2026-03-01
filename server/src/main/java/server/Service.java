package server;

import dataaccess.DataAccessException;

public abstract class Service {
    public abstract void clear() throws DataAccessException;
}