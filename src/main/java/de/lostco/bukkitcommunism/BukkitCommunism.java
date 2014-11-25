package de.lostco.bukkitcommunism;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
//import com.earth2me.essentials.api.IItemDB;

public class BukkitCommunism extends JavaPlugin {
	
	HashMap<Material, Integer> vault = new HashMap<Material, Integer>();
	
	@Override
	public void onEnable() {
		getLogger().info("BukkitCommunism Enabled!");
		for (String str: getConfig().getKeys(true)) {
			vault.put(Material.matchMaterial(str), getConfig().getInt(str));
		}
	}
	@Override
	public void onDisable() {
		for (Entry<Material, Integer> vaultEntry: vault.entrySet()) {
			getConfig().set(vaultEntry.getKey().name(), vaultEntry.getValue());
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("BukkitCommunism") && (sender.hasPermission("bukkitcommunism.*") || sender.hasPermission(cmd.getPermission()))) {
			sender.sendMessage("You triggered BukkitCommunism! Comrade Stalin salutes you!");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("bcd") && (sender.hasPermission("bukkitcommunism.*") || sender.hasPermission(cmd.getPermission()))) {
			sender.sendMessage("Attempting to deposit: " + ((Player)(sender)).getItemInHand().getAmount() + " " + ((Player)(sender)).getItemInHand());
			Material type = ((Player)(sender)).getItemInHand().getType();
			int amount = ((Player)(sender)).getItemInHand().getAmount();
			if (vault.containsKey(type)) {
				getLogger().info("DEBUG: Vault contains key!");
				vault.put(type, vault.get(type) + amount);
			} else {
				vault.put(type, amount);
			}
			//((Player)sender).setItemInHand(new ItemStack(type, 0));
			((Player)sender).setItemInHand(null);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("bcw") && (sender.hasPermission("bukkitcommunism.*") || sender.hasPermission(cmd.getPermission()))) {
			if (args.length < 1) {
				getLogger().info("Less than 1 argument!");
				return false;
			}
			Material type = Material.matchMaterial(args[0]);
			getLogger().info("got material match");
			int amount;
			if (args.length < 2) {
				getLogger().info("Less than 2 arguments!");
				amount = 64;
			} else {
				amount = Integer.parseInt(args[1]);
			}
			getLogger().info("got amount");
			if (!vault.containsKey(type)) {
				sender.sendMessage("That item is not in the bank!");
			} else if (vault.get(type) < amount) {
				sender.sendMessage("Not enough available! There are only " + vault.get(type) + " of " + type.name() + " available.");
			} else {
				getLogger().info("Enough items available...");
				vault.put(type, vault.get(type) - amount);
				getLogger().info("Withdrew from vault");
				HashMap<Integer, ItemStack> spareItems = ((Player)sender).getInventory().addItem(new ItemStack(type, amount));
				getLogger().info("Gave items to player, placed overflow in vault");
				for (Entry<Integer, ItemStack> i : spareItems.entrySet()) {
					if (vault.containsKey(type)) {
						vault.put(i.getValue().getType(), vault.get(i.getValue().getType()) + i.getValue().getAmount());
					} else { 
						vault.put(i.getValue().getType(), i.getValue().getAmount());
					}
				}
			}
			sender.sendMessage("Withdrew: " + amount + " " + type.name());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("bcset") && (sender.hasPermission("bukkitcommunism.*") || sender.hasPermission(cmd.getPermission()))) {
			if (args.length < 2) {
				return false;
			}
			sender.sendMessage("Attempting to set: " + args[1] + " " + args[0]);
			Material type = Material.matchMaterial(args[0]);
			getLogger().info("got material");
			int amount = Integer.parseInt(args[1]);
			getLogger().info("got amount");
			vault.put(type, amount);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("bcget") && (sender.hasPermission("bukkitcommunism.*") || sender.hasPermission(cmd.getPermission()))) {
			getLogger().info("used bcget");
			Material type = Material.matchMaterial(args[0]);
			getLogger().info("got material match");
			int amount = 0;
			if (vault.containsKey(type)) {
				amount = vault.get(type);
				getLogger().info("found amount");
			}
			sender.sendMessage("There are: " + amount + " of " + type.name());
			getLogger().info("reported stock");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("bcname") && (sender.hasPermission("bukkitcommunism.*") || sender.hasPermission(cmd.getPermission()))) {
			Material type = ((Player)(sender)).getItemInHand().getType();
			sender.sendMessage("Material name: " + type.name());
			return true;
		}
		return false;
	}
}
