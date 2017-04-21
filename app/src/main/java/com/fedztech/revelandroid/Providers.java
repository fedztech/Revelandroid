/*  Revelandroid - An app for the Revelation Password Manager data.
    Copyright (C) 2013  Juan Carlos Garza

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
package com.fedztech.revelandroid;

/*
 * Representation of the different data sources where the password files 
 * can be stored.
 */
public class Providers {
	
	/*
	 * Enlists the supported providers
	 * 		PRO_LOCAL is for using the External Storage
	 * 		PRO_DROPBOX is for Dropbox
	 */
	public enum EProviders
	{
		PRO_LOCAL,				
		PRO_DROPBOX,
	};
	
	Providers()
	{}
	
	/*
	 * Given a string, it returns the corresponding Provider enum item.
	 * @param providerString The provider as text.
	 * @return Corresponding enum from EProviders
	 */
	public EProviders getProviderId(String providerString)
	{
		EProviders provider = EProviders.PRO_LOCAL;
		if(providerString.compareTo("Local")==0)
		{
			provider = EProviders.PRO_LOCAL;
		}
		if(providerString.compareTo("Dropbox")==0)
		{
			provider = EProviders.PRO_DROPBOX;
		}	
		return provider;
	}
	
	/*
	 * Given an enum item from the EProviders, it returns the corresponding name string.
	 */
	public String getProviderString(EProviders id)
	{
		String retVal = "";
		switch(id)
		{
			case PRO_LOCAL:
			{
				retVal = "Local";
			}break;
			case PRO_DROPBOX:
			{
				retVal = "Dropbox";
			}break;
		}
		return retVal;
	}
}
