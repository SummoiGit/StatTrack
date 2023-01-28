package my.plugin.main.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import plugin.stattrack.main.StatTrack;

public class OpenCommand implements CommandExecutor {
	
	public static Player p;
	
	public static Inventory inv = Bukkit.createInventory(p, 9, ChatColor.translateAlternateColorCodes('&', "&3&k&l000 &4&lAdmin Giver &3&k&l000"));

    public static ItemStack book1 = new ItemStack(Material.BOOK);
    public static ItemMeta book1Meta = book1.getItemMeta();
    public static ItemStack book2 = new ItemStack(Material.BOOK);
    public static ItemMeta book2Meta = book2.getItemMeta();
	
	public OpenCommand(StatTrack plugin) {
		plugin.getCommand("sopen").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED+"Only players can execute this command!");
			return true;
		}
		Player p = (Player) sender;
		
		if (p.hasPermission("StatTrack.sopen")) {
			
			String blockscounterconfig = StatTrack.plugin.getConfig().getString("blocks_counter_displayname");
		    String blockscounter = ChatColor.translateAlternateColorCodes('&', blockscounterconfig);
		    book1Meta.setDisplayName(blockscounter);
		    ArrayList<String> lore = new ArrayList<String>();
		    String blockscounterloreconfig = StatTrack.plugin.getConfig().getString("blocks_counter_lore");
		    String blockscounterlore = ChatColor.translateAlternateColorCodes('&', blockscounterloreconfig);
		    lore.add(blockscounterlore);
		    book1Meta.setLore(lore);
		    book1.setItemMeta(book1Meta);
		    
	        String soulscounterconfig = StatTrack.plugin.getConfig().getString("souls_counter_displayname");
	        String soulscounter = ChatColor.translateAlternateColorCodes('&', soulscounterconfig);
	        book2Meta.setDisplayName(soulscounter);
	        ArrayList<String> lore2 = new ArrayList<String>();
	        String soulscounterloreconfig = StatTrack.plugin.getConfig().getString("souls_counter_lore");
	        String soulscounterlore = ChatColor.translateAlternateColorCodes('&', soulscounterloreconfig);
	        lore2.add(soulscounterlore);
	        book2Meta.setLore(lore2);
	        book2.setItemMeta(book2Meta);
			
			inv.setItem(0, book1);
			inv.setItem(1, book2);
			
			p.openInventory(inv);
			return true;
		} else {
			p.sendMessage(ChatColor.RED+"You do not have permission to execute this command!");
		}
		return false;
	}

}
