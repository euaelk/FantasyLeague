package com.example.fantasynba.mocking;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.repository.PlayerRepository;
import com.example.fantasynba.service.PlayerServiceImpl;
import com.example.fantasynba.service.TeamService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlayerServiceTest {

    private PlayerServiceImpl playerService;
    private TeamService teamService;
    private PlayerRepository playerRepository;

    @Before
    public void setup(){
        teamService = mock(TeamService.class);
        playerRepository = mock(PlayerRepository.class);
        playerService = new PlayerServiceImpl(playerRepository, teamService);
    }

    @Test
    public void findPlayerMethod_Test(){
        Player player = new Player();
        player.setName("Kevin Durant");
        player.setDob("April 1 1992");
        player.setPosition("SF");
        player.setWeight(210);
        player.setCollege("Texas A&M");

        when(playerRepository.findByName("Kevin Durant")).thenReturn(player);

        String playerName = playerService.findPlayer("Kevin Durant").getName();
        assertEquals(playerName, player.getName());
    }
    @Test
    public void calculateAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500);
            cf.complete("Hello");
            return null;
        });
        String result = cf.get();
        assertEquals("Hello", result);
    }

    @Test
    public void supplySync_StringTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
        // lamda function creating Future instance of 'Hello'
        assertEquals("Hello", future.get());
    }

    @Test
    public void thenApply_FromSupplyAsyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> then = future.thenApply(s -> s + " World");
        // thenApply accepts Function instance from supplyAsync
        // uses it to process the result and return a Future that holds a value returned from the function
        assertEquals("Hello World", then.get());
    }

    @Test
    public void thenAccept_Void() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = cf.thenAccept(s -> System.out.println("Computation returned: "+ s));

        future.get();
    }

    @Test
    public void dontReturnComputation_NorAnyValue() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = cf.thenRun(() -> System.out.println("Computation finished"));
        future.get();
    }

    @Test
    public void combiningFutures_ComposeMethod() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));
        // thenCompose takes a fxn that returns a cf instance
        assertEquals("Hello World", cf.get());
    }

    @Test
    public void combiningFutures_CombineMethod() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCombine(CompletableFuture.supplyAsync(() -> " World"),
                        (s1, s2) -> s1 + s2);
        // thenCombine executes two independent Futures and does something w/ them

        assertEquals("Hello World", cf.get());
    }
}
