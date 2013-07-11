package unwrittenfun.bukkit.lukkit.commandsender;

import org.bukkit.command.CommandSender;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import unwrittenfun.bukkit.lukkit.LukkitObject;


public class GetNameFunction extends VarArgFunction {

	@Override
	public Varargs invoke(Varargs args) {
		return LuaValue.valueOf(((CommandSender) ((LukkitObject) args.arg(1)).getObject()).getName());
	}
	
}
