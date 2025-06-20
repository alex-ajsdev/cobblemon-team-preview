package net.ajsdev.cobblemonteampreview.scheduler

import kotlinx.coroutines.*
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import java.util.concurrent.TimeUnit

object Scheduler : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Default + job

    private val serverRef = CompletableDeferred<MinecraftServer>()

    fun init() {
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            serverRef.complete(server)
        }

        ServerLifecycleEvents.SERVER_STOPPED.register {
            job.cancel()
        }
    }

    fun schedule(delayTicks: Int, task: MinecraftServer.() -> Unit): Job {
        return launch {
            delay(ticksToMillis(delayTicks))
            val server = serverRef.await()
            server.execute {
                task(server)
            }
        }
    }

    private fun ticksToMillis(ticks: Int): Long =
        TimeUnit.MILLISECONDS.convert(ticks.toLong() * 50, TimeUnit.MILLISECONDS)
}
