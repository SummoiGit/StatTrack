package plugin.stattrack.main;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class StatTrackHandler implements Listener {
	
    public static void spawnFireworks(Location location, int amount){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        fw.setMetadata("nodamage", new FixedMetadataValue(StatTrack.plugin, true));
        FireworkMeta fwm = fw.getFireworkMeta();
       
        fwm.setPower(10);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
       
        fw.setFireworkMeta(fwm);
        fw.detonate();
       
        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }
    
    @EventHandler
    public void disableFireworkDamage(EntityDamageByEntityEvent e) {
    	if (e.getDamager() instanceof Firework) {
            Firework fw = (Firework) e.getDamager();
            if (fw.hasMetadata("nodamage")) {
                e.setCancelled(true);
            }
        }
    }

	@EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getKiller();
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().getLore();
            int blocksBroken = 0;
        	if (event.getEntity().getKiller().equals(player)) {
        		if (lore != null) {
                	for (String line : lore) {
                        if (line.startsWith(ChatColor.GRAY + "Souls: ")) {
                            String[] parts = line.split(" ");
                            blocksBroken = Integer.parseInt(ChatColor.stripColor(parts[1].replaceAll(",", "")));
                            blocksBroken++;
                            NumberFormat formatter = NumberFormat.getIntegerInstance();
                            formatter.setGroupingUsed(true);
                            String newLine = ChatColor.GRAY + "Souls: " + ChatColor.RED + formatter.format(blocksBroken);
                            int index = lore.indexOf(line);
                            if(index != -1) {
                                lore.set(index, newLine);
                                meta.setLore(lore);
                                item.setItemMeta(meta);
                                player.getInventory().setItemInMainHand(item);
                            }
                            break;
                        }
                    }
                }
        	}
        }
    }
	
	
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        
        ItemStack book1 = new ItemStack(Material.BOOK);
        ItemMeta book1Meta = book1.getItemMeta();
        String blockscounterconfig = StatTrack.plugin.getConfig().getString("blocks_counter_displayname");
        String blockscounter = ChatColor.translateAlternateColorCodes('&', blockscounterconfig);
        book1Meta.setDisplayName(blockscounter);
        ArrayList<String> lore = new ArrayList<String>();
        String blockscounterloreconfig = StatTrack.plugin.getConfig().getString("blocks_counter_lore");
        String blockscounterlore = ChatColor.translateAlternateColorCodes('&', blockscounterloreconfig);
        lore.add(blockscounterlore);
        book1Meta.setLore(lore);
        book1.setItemMeta(book1Meta);

        ItemStack book2 = new ItemStack(Material.BOOK);
        ItemMeta book2Meta = book2.getItemMeta();
        String soulscounterconfig = StatTrack.plugin.getConfig().getString("souls_counter_displayname");
        String soulscounter = ChatColor.translateAlternateColorCodes('&', soulscounterconfig);
        book2Meta.setDisplayName(soulscounter);
        ArrayList<String> lore2 = new ArrayList<String>();
        String soulscounterloreconfig = StatTrack.plugin.getConfig().getString("souls_counter_lore");
        String soulscounterlore = ChatColor.translateAlternateColorCodes('&', soulscounterloreconfig);
        lore2.add(soulscounterlore);
        book2Meta.setLore(lore2);
        book2.setItemMeta(book2Meta);

		if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            if (cursor.equals(book1) && current.getType().name().endsWith("_PICKAXE") ||
            		cursor.equals(book1) && current.getType().name().endsWith("_SHOVEL") ||
            		cursor.equals(book1) && current.getType().name().endsWith("_AXE")) {
            	ItemMeta meta = current.getItemMeta();
                List<String> lore1;
                if (meta.hasLore()) {
                	event.setCancelled(true);
                    lore1 = meta.getLore();
                } else {
                    lore1 = new ArrayList<String>();
                    Location playerLocation = player.getLocation();
                	player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, playerLocation, 10);
        			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 29);
        	        String blockscountermessageconfig = StatTrack.plugin.getConfig().getString("souls_counter_message");
        	        String blockscountermessage = ChatColor.translateAlternateColorCodes('&', blockscountermessageconfig);
        			player.sendMessage(ChatColor.translateAlternateColorCodes('&', blockscountermessage));
                	cursor.setAmount(current.getAmount() - 1);

                	spawnFireworks(player.getLocation(), 1);
                    
                    Firework f = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
                    FireworkMeta fd = f.getFireworkMeta();
                    fd.addEffects(FireworkEffect.builder()
                            .flicker(false)
                            .trail(true)
                            .with(Type.BALL)
                            .withColor(Color.BLUE)
                            .withFade(Color.WHITE)
                            .build());
                    int blocksBroken = 0;
                    lore1.add(ChatColor.translateAlternateColorCodes('&', "&7 "));
                    lore1.add(ChatColor.translateAlternateColorCodes('&', "&7Mined: ") + ChatColor.RED + blocksBroken); // change this line
                    lore1.add(ChatColor.translateAlternateColorCodes('&', "&7 "));
                    meta.setLore(lore1);
                    current.setItemMeta(meta);
                }
            }
            if (cursor.equals(book2) && current.getType().name().endsWith("_SWORD") ||
            		cursor.equals(book2) && current.getType().name().endsWith("_AXE")) {
            	
            	ItemMeta meta = current.getItemMeta();
                List<String> lore1;
                if (meta.hasLore()) {
                	event.setCancelled(true);
                    lore1 = meta.getLore();
                } else {
                    lore1 = new ArrayList<String>();
                	Location playerLocation = player.getLocation();
                	player.getWorld().spawnParticle(Particle.WARPED_SPORE, playerLocation, 100);
        			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 29);
        	        String blockscountermessageconfig = StatTrack.plugin.getConfig().getString("souls_counter_message");
        	        String blockscountermessage = ChatColor.translateAlternateColorCodes('&', blockscountermessageconfig);
        			player.sendMessage(ChatColor.translateAlternateColorCodes('&', blockscountermessage));
                	cursor.setAmount(current.getAmount() - 1);
                	
                	spawnFireworks(player.getLocation(), 1);
                    
                    Firework f = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
                    FireworkMeta fd = f.getFireworkMeta();
                    fd.addEffects(FireworkEffect.builder()
                            .flicker(false)
                            .trail(true)
                            .with(Type.BALL)
                            .withColor(Color.BLUE)
                            .withFade(Color.WHITE)
                            .build());
                    int blocksBroken = 0;
                    lore1.add(ChatColor.translateAlternateColorCodes('&', "&7 "));
                    lore1.add(ChatColor.translateAlternateColorCodes('&', "&7Souls: ") + ChatColor.RED + blocksBroken); // change this line
                    lore1.add(ChatColor.translateAlternateColorCodes('&', "&7 "));
                    meta.setLore(lore1);
                    current.setItemMeta(meta);
                }
            }
        }
    }
    
    @EventHandler
    public void invListener(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();
        InventoryView invv = event.getView();
        
        ItemStack book1 = new ItemStack(Material.BOOK);
        ItemMeta book1Meta = book1.getItemMeta();
        String blockscounterconfig = StatTrack.plugin.getConfig().getString("blocks_counter_displayname");
        String blockscounter = ChatColor.translateAlternateColorCodes('&', blockscounterconfig);
        book1Meta.setDisplayName(blockscounter);
        ArrayList<String> lore = new ArrayList<String>();
        String blockscounterloreconfig = StatTrack.plugin.getConfig().getString("blocks_counter_lore");
        String blockscounterlore = ChatColor.translateAlternateColorCodes('&', blockscounterloreconfig);
        lore.add(blockscounterlore);
        book1Meta.setLore(lore);
        book1.setItemMeta(book1Meta);

        ItemStack book2 = new ItemStack(Material.BOOK);
        ItemMeta book2Meta = book2.getItemMeta();
        String soulscounterconfig = StatTrack.plugin.getConfig().getString("souls_counter_displayname");
        String soulscounter = ChatColor.translateAlternateColorCodes('&', soulscounterconfig);
        book2Meta.setDisplayName(soulscounter);
        ArrayList<String> lore2 = new ArrayList<String>();
        String soulscounterloreconfig = StatTrack.plugin.getConfig().getString("souls_counter_lore");
        String soulscounterlore = ChatColor.translateAlternateColorCodes('&', soulscounterloreconfig);
        lore2.add(soulscounterlore);
        book2Meta.setLore(lore2);
        book2.setItemMeta(book2Meta);

        
        if(invv.getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&3&k&l000 &4&lAdmin Giver &3&k&l000")) && inv.equals(player.getOpenInventory().getTopInventory())) {
        	event.setCancelled(true);
        	if (event.getSlot() == 0) {
        		player.getInventory().addItem(book1);
        	} else if (event.getSlot() == 1) {
        		player.getInventory().addItem(book2);
        	}
        }
        
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = item.getItemMeta().getLore();
        int blocksBroken = 0;
        if (lore != null) {
            for (String line : lore) {
                if (line.startsWith(ChatColor.GRAY + "Mined: ")) {
                    String[] parts = line.split(" ");
                    blocksBroken = Integer.parseInt(ChatColor.stripColor(parts[1].replaceAll(",", "")));
                    blocksBroken++;
                    NumberFormat formatter = NumberFormat.getIntegerInstance();
                    formatter.setGroupingUsed(true);
                    String newLine = ChatColor.GRAY + "Mined: " + ChatColor.RED + formatter.format(blocksBroken);
                    int index = lore.indexOf(line);
                    if(index != -1) {
                        lore.set(index, newLine);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        player.getInventory().setItemInMainHand(item);
                    }
                    break;
                }
            }
        }
    }

}
