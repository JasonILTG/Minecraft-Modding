package com.JasonILTG.ScienceMod.reference.chemistry;

import java.util.HashSet;
import java.util.Set;

import com.JasonILTG.ScienceMod.reference.MatterState;

/**
 * Enum for all of the elements and their data.
 * 
 * @author JasonILTG and syy1125
 */
public enum Element
{
	// Period 1
	HYDROGEN("Hydrogen", "H"), HELIUM("Helium", "He"),
	// Period 2
	LITHIUM("Lithium", "Li"), BERYLLIUM("Beryllium", "Be"), BORON("Boron", "Bo"), CARBON("Carbon", "C"),
	NITROGEN("Nitrogen", "N"), OXYGEN("Oxygen", "O"), FLUORINE("Fluorine", "F"), NEON("Neon", "Ne"),
	// Period 3
	SODIUM("Sodium", "Na"), MAGNESIUM("Magnesium", "Mg"), ALUMINIUM("Aluminium", "Al"), SILICON("Silicon", "Si"),
	PHOSPHORUS("Phosphorus", "P"), SULFUR("Sulfur", "S"), CHLORINE("Chlorine", "Cl"), ARGON("Argon", "Ar"),
	// Period 4
	POTASSIUM("Potassium", "K"), CALCIUM("Calcium", "Ca"), SCANDIUM("Scandium", "Sc"), TITANIUM("Titanium", "Ti"),
	VANADIUM("Vanadium", "V"), CHROMIUM("Chromium", "Cr"), MANGANESE("Manganese", "Mn"), IRON("Iron", "Fe"), COBALT("Cobalt", "Co"),
	NICKEL("Nickel", "Ni"), COPPER("Copper", "Cu"), ZINC("Zinc", "Zn"), GALLIUM("Gallium", "Ga"), GERMANIUM("Germanium", "Ge"),
	ARSENIC("Arsenic", "As"), SELENIUM("Selenium", "Se"), BROMINE("Bromine", "Br"), KRYPTON("Krypton", "Kr"),
	// Period 5
	RUBIDIUM("Rubidium", "Rb"), STRONTIUM("Strontium", "Sr"), YTTRIUM("Yttrium", "Y"), ZIRCONIUM("Zirconium", "Zr"),
	NIOBIUM("Niobium", "Nb"), MOLYBDENUM("Molybdenum", "Mo"), TECHNETIUM("Technetium", "Tc"), RUTHENIUM("Ruthenium", "Ru"),
	RHODIUM("Rhodium", "Rh"), PALLADIUM("Palladium", "Pd"), SILVER("Silver", "Ag"), CADMIUM("Cadmium", "Cd"), INDIUM("Indium", "In"),
	TIN("Tin", "Sn"), ANTIMONY("Antimony", "Sb"), TELLURIUM("Tellurium", "Te"), IODINE("Iodine", "I"), XENON("Xenon", "Xe"),
	// Period 6
	CESIUM("Cesium", "Cs"), BARIUM("Barium", "Ba"), LANTHANUM("Lanthanum", "La"), CERIUM("Cerium", "Ce"),
	PRASEODYMIUM("Praseodymium", "Pr"), NEODYMIUM("Neodymium", "Nd"), PROMETHIUM("Promethium", "Pm"), SAMARIUM("Samarium", "Sm"),
	EUROPIUM("Europium", "Eu"), GADOLINIUM("Gadolinium", "Gd"), TERBIUM("Terbium", "Tb"), DYSPROSIUM("Dysprosium", "Dy"),
	HOLMIUM("Holmium", "Ho"), ERBIUM("Erbium", "Er"), THULIUM("Thulium", "Tm"), YTTERBIUM("Ytterbium", "Yb"),
	LUTETIUM("Lutetium", "Lu"), HAFNIUM("Hafnium", "Hf"), TANTALUM("Tantalum", "Ta"), TUNGSTEN("Tungsten", "W"),
	RHENIUM("Rhenium", "Re"), OSMIUM("Osmium", "Os"), IRIDIIUM("Iridium", "Ir"), PLATINUM("Platinum", "Pt"), GOLD("Gold", "Au"),
	MERCURY("Mercury", "Hg"), THALLIUM("Thallium", "Tl"), LEAD("Lead", "Pb"), BISMUTH("Bismuth", "Bi"), POLONIUM("Polonium", "Po"),
	ASTATINE("Astatine", "At"), RADON("Radon", "Rn"),
	// Period 7
	FRANCIUM("Francium", "Fr"), RADIUM("Radium", "Ra"), ACTINIUM("Actinium", "Ac"), THORIUM("Thorium", "Th"),
	PROTACTINIUM("Protactinium", "Pa"), URANIUM("Uranium", "U"), NEPTUNIUM("Neptunium", "Np"), PLUTONIUM("Plutonium", "Pu"),
	AMERICIUM("Americium", "Am"), CURIUM("Curium", "Cm"), BERKELIUM("Berkelium", "Bk"), CALIFORNIUM("Californium", "Cf"),
	EINSTEINIUM("Einsteinium", "Es"), FERMIUM("Fermium", "Fm"), MENDELEVIUM("Mendelevium", "Md"), NOBELIUM("Nobelium", "No"),
	LAWRENCIUM("Lawrencium", "Lr"), RUTHERFORDIUM("Rutherfordium", "Rf"), DUBNIUM("Dubnium", "Db"), SEABORGIUM("Seaborgium", "Sg"),
	BOHRIUM("Bohrium", "Bh"), HASSIUM("Hassium", "Hs"), MEITNERIUM("Meitnerium", "Mt"), DARMSTADTIUM("Darmstadtium", "Ds"),
	ROENTGENIUM("Roentgenium", "Rg"), COPERNICIUM("Copernicium", "Cn"), UNUNTRIUM("Ununtrium", "Uut"), FLEROVIUM("Flerovium", "Fl"),
	UNUNPENTIUM("Ununpentium", "Uup"), LIVERMORIUM("Livermorium", "Lv"), UNUNSEPTIUM("Ununseptium", "Uus"),
	UNUNOCTIUM("Ununoctium", "Uuo");
	
	private String name;
	private String lowerCaseName;
	private String symbol;
	private Set<Ion> ionizedForms;
	/** The array of ions that this element has */
	private Ion[] ionArray;
	
	// Elements that are naturally polyatomic
	public static final int[] polyatomics = { 0, 6, 7, 8, 16, 34 };
	// Elements that are naturally gaseous
	public static final int[] gases = { 0, 1, 6, 7, 8, 9, 16, 17, 35, 53, 85 };
	// Elements that are naturally liquid
	public static final int[] liquids = { 34, 79 };
	// All other elements are assumed to be naturally solid
	
	/** An instance array for faster access */
	public static final Element[] VALUES = values();
	public static final int ELEMENT_COUNT = VALUES.length;
	
	private Element(String elementName, String elementSymbol)
	{
		name = elementName;
		lowerCaseName = elementName.toLowerCase();
		symbol = elementSymbol;
		ionizedForms = new HashSet<Ion>();
		ionArray = null;
	}
	
	/**
	 * @return The element's atomic number
	 */
	public int getAtomicNumber()
	{
		return ordinal() + 1;
	}
	
	/**
	 * @return The symbol of the element
	 */
	public String getElementSymbol()
	{
		return symbol;
	}
	
	/**
	 * @return The name of the element
	 */
	public String getElementName()
	{
		return name;
	}
	
	/**
	 * @return The symbolic notation for the element's compound
	 */
	public String getElementCompound()
	{
		for (int p : polyatomics)
		{
			if (ordinal() == p)
			{
				return getElementSymbol() + "2";
			}
		}
		return getElementSymbol();
	}
	
	/**
	 * @return The state of matter the element is in naturally
	 */
	public MatterState getElementState()
	{
		int index = ordinal();
		for (int g : gases)
		{
			if (index == g)
			{
				return MatterState.GAS;
			}
		}
		for (int l : liquids)
		{
			if (index == l)
			{
				return MatterState.LIQUID;
			}
		}
		return MatterState.SOLID;
	}
	
	/**
	 * @return The unlocalized name
	 */
	public String getUnlocalizedName()
	{
		return lowerCaseName;
	}
	
	/**
	 * Returns an element given its atomic number.
	 * 
	 * @param atomicNumber The atomic number of the element
	 * @return The element
	 */
	public static Element getElementByAtomicNumber(int atomicNumber)
	{
		if (atomicNumber < 0 || atomicNumber > ELEMENT_COUNT) return null;
		
		// Indexes are one lower that atomic number.
		return VALUES[atomicNumber - 1];
	}
	
	/**
	 * Records an ionized form of this element. (Package access is intended.)
	 * 
	 * @param i The ion to record.
	 */
	void addIon(Ion i)
	{
		ionizedForms.add(i);
		ionArray = ionizedForms.toArray(new Ion[ionizedForms.size()]);
	}
	
	public Ion[] getIons()
	{
		return ionArray;
	}
}
