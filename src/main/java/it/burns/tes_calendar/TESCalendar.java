package it.burns.tes_calendar;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class TESCalendar implements ModInitializer {
    public static final String MOD_ID = "dremily_tes_calendar";

    public static final String[] MONTH_NAMES = { "Morning Star", "Sun's Dawn", "First Seed", "Rain's Hand", "Second Seed", "Midyear", "Sun's Height", "Last Seed", "Hearthfire", "Frostfall", "Sun's Dusk", "Evening Star" };
    public static final String[] DAY_NAMES = { "Morndas", "Tirdas", "Middas", "Turdas", "Fredas", "Loredas", "Sundas" };
    public static final int[] MONTH_LENGTHS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public static final long TICKS_IN_DAY = 24000;
    public static final long TICKS_IN_YEAR = 365 * TICKS_IN_DAY;
    public static final long TICKS_IN_ERA = TICKS_IN_YEAR * 100;

    public static final String OUTPUT_FORMAT = "%s, %s%s day of %s, %dE %d";

    public static long PREV_DAY;
    public static boolean NEXT_DAY;

    private static String Nth( final int num ){
        if ( num < 1 || num > 31 ){
            return "wtf";
        }

        if ( num >= 11 && num <= 13){
            return "th";
        }

        return switch (num % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

    public static Text SolveMessage(long time ){
        time += 1776000; //Seasons mods start on Spring 1 typically, which is March 14 / March 15. Not account for this makes the months weird


        int dow, month, day, era, year, day2;

        era = (int)(time / TICKS_IN_ERA);
        time -= ( era * TICKS_IN_ERA);

        year = (int)(time / TICKS_IN_YEAR);
        time -= (year * TICKS_IN_YEAR);

        day = (int)(time / TICKS_IN_DAY);
        day2 = day;
        month = 0;

        int i;

        for ( i = 0; i < 12; i++){
            if ( day > MONTH_LENGTHS[i]){
                day -= MONTH_LENGTHS[i];
                month = i;
            }
            else {
                break;
            }
        }

        if ( day == 0 ){ day++; }

        dow = day2 % 7;

        return Text.of(String.format(OUTPUT_FORMAT, DAY_NAMES[dow], day, Nth( day ), MONTH_NAMES[month], ++era, ++year  ) );
    }

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register( (MinecraftServer server)->{
            ServerWorld wld = server.getWorld(ServerWorld.OVERWORLD);

            long thisTick;
            long thisDay;

            if ( wld == null ){
                return;
            }

            thisTick = wld.getTime();

            thisDay = thisTick / 24000;

            if ( PREV_DAY == 0 ){
                PREV_DAY = thisDay;
            }

            if ( PREV_DAY != thisDay){
             NEXT_DAY = true;
             PREV_DAY = thisDay;
            }

            if ( wld.getTimeOfDay() == 1000 || ( NEXT_DAY && ( wld.getTimeOfDay() >= 1000 ) ) ) {
                NEXT_DAY = false;
                for ( ServerPlayerEntity e : server.getPlayerManager().getPlayerList() ){
                    e.sendMessage(SolveMessage(thisTick));
                }
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register( (Entity e, ServerWorld world)->{
            if (e.isPlayer()){
                e.sendMessage( SolveMessage( world.getTime()));
            }
        });
    }
}
