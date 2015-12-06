package com.JasonILTG.ScienceMod.reference;

/**
 * Enum for the states of matter
 * 
 * @author JasonILTG and syy1125
 */
public enum MatterState
{
	SOLID("Solid"), LIQUID("Liquid"), GAS("Gas");
	
	private String name;
	
	private MatterState(String stateName)
	{
		name = stateName;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Used for tooltip, but for general use, getName() recommended.
	 */
	@Override
	public String toString()
	{
		return name;
	}
	
	/**
	 * Returns the abbreviation of the state
	 */
	public String getShortName()
	{
		return name.substring(0, 1).toLowerCase();
	}
}
