package com.JasonILTG.ScienceMod.block.machines;

import com.JasonILTG.ScienceMod.ScienceMod;
import com.JasonILTG.ScienceMod.reference.EnumGUI;
import com.JasonILTG.ScienceMod.reference.Names;
import com.JasonILTG.ScienceMod.tileentity.machines.TEMixer;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Machine that mixes solutions and mixtures.
 * 
 * @author JasonILTG and syy1125
 */
public class Mixer extends MachineScience
{
	/**
	 * Default constructor.
	 */
	public Mixer()
	{
		super(Material.iron);
		setUnlocalizedName(Names.Blocks.Machine.MACHINE_MIXER);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		TileEntity mixerEntity = new TEMixer();
		mixerEntity.setWorldObj(worldIn);
		return mixerEntity;
	}
	
	@Override
	public int getRenderType()
	{
		return 3;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX,
			float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(ScienceMod.modInstance, EnumGUI.MIXER.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
}