package org.codelibs.fess.entity;

import java.io.Serializable;

public interface FessUser extends Serializable {

    String getName();

    String[] getRoleNames();

    String[] getGroupNames();

}
