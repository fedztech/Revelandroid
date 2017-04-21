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

/// @brief Interface to be implemented by RevelationData classes.
public interface RevelationDataIf {

    /// @brief Decrypts the rawData given a password and generates the tree.
    /// @param password The password used to decrypt.
    /// @param rawData The data to decrypt.
    /// @return nothing. Can throw RevelationData_Exception if something occurs.
    public void processEncryptedData(byte[] password, byte[] rawData) throws RevelationData_Exception;
}
