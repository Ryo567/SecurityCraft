package net.geforcemods.securitycraft.imc.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WailaDataProvider implements IWailaDataProvider {

	public static void callbackRegister(IWailaRegistrar registrar){
		SecurityCraft.log("Adding Waila support!");

		registrar.addConfig("SecurityCraft", "securitycraft.showowner", ClientUtils.localize("waila.securitycraft:displayOwner"));
		registrar.addConfig("SecurityCraft", "securitycraft.showmodules", ClientUtils.localize("waila.securitycraft:showModules"));
		registrar.addConfig("SecurityCraft", "securitycraft.showpasswords", ClientUtils.localize("waila.securitycraft:showPasswords"));
		registrar.addConfig("SecurityCraft", "securitycraft.showcustomname", ClientUtils.localize("waila.securitycraft:showCustomName"));
		registrar.registerBodyProvider(new WailaDataProvider(), IOwnable.class);
		registrar.registerStackProvider(new WailaDataProvider(), ICustomWailaDisplay.class);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config) {
		if(data.getBlock() instanceof ICustomWailaDisplay)
			return ((ICustomWailaDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> tipList, IWailaDataAccessor iDataAccessor, IWailaConfigHandler iConfigHandler) {
		return tipList;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tipList, IWailaDataAccessor iDataAccessor, IWailaConfigHandler iConfigHandler) {
		if(iDataAccessor.getBlock() instanceof ICustomWailaDisplay && !((ICustomWailaDisplay) iDataAccessor.getBlock()).shouldShowSCInfo(iDataAccessor.getWorld(), iDataAccessor.getBlockState(), iDataAccessor.getPosition())) return tipList;

		if(iConfigHandler.getConfig("securitycraft.showowner") && iDataAccessor.getTileEntity() instanceof IOwnable)
			tipList.add(ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) iDataAccessor.getTileEntity()).getOwner().getName());

		if(iConfigHandler.getConfig("securitycraft.showmodules") && iDataAccessor.getTileEntity() instanceof CustomizableSCTE && ((CustomizableSCTE) iDataAccessor.getTileEntity()).getOwner().isOwner(iDataAccessor.getPlayer())){
			if(!((CustomizableSCTE) iDataAccessor.getTileEntity()).getModules().isEmpty())
				tipList.add(ClientUtils.localize("waila.securitycraft:equipped"));

			for(EnumCustomModules module : ((CustomizableSCTE) iDataAccessor.getTileEntity()).getModules())
				tipList.add("- " + module.getName());
		}

		if(iConfigHandler.getConfig("securitycraft.showpasswords") && iDataAccessor.getTileEntity() instanceof IPasswordProtected && !(iDataAccessor.getTileEntity() instanceof TileEntityKeycardReader) && ((IOwnable) iDataAccessor.getTileEntity()).getOwner().isOwner(iDataAccessor.getPlayer())){
			String password = ((IPasswordProtected) iDataAccessor.getTileEntity()).getPassword();

			tipList.add(ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet")));
		}

		if(iConfigHandler.getConfig("securitycraft.showcustomname") && iDataAccessor.getTileEntity() instanceof INameable && ((INameable) iDataAccessor.getTileEntity()).canBeNamed()){
			String name = ((INameable) iDataAccessor.getTileEntity()).getCustomName();

			tipList.add(ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) iDataAccessor.getTileEntity()).hasCustomName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet")));
		}

		return tipList;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemstack, List<String> tail, IWailaDataAccessor data, IWailaConfigHandler config) {
		return tail;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tagCompound, World world, BlockPos pos) {
		return tagCompound;
	}

}