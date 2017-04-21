/*  Revelandroid - An app for the Revelation Password Manager data.
    Copyright (C) 2013-2015  Juan Carlos Garza

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.fedztech.revelandroid.data;

/**
 * Enumeration enlisting the supported data types
 */
public enum RevelationDataTypes {
    V1(1),      ///< Original version
    V2(2);      ///< New more secure 2nd version.

    private final int id;
    RevelationDataTypes(int id) {
        this.id = id;
    }
    public int id() { return id; }
}
