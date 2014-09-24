package problem;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for managing tours
 * @author Joshua Song
 *
 */
public class Tour {
	
	public enum Status {
		PREPARING, RACING, FINISHED
	}
	
	private Setup setup;
	/** The current race number. */
	private int raceNo;
	/** Maximum number of races in the tour */
	private int maxRaces;
	/** The current amount of money */
	private double money;
	/** List of cycles purchased */
	private ArrayList<Cycle> purchasedCycles;
	/** List of RaceSims. For runnning simulations and for recording history */
	private List<RaceSim> raceSims;
	/** History of tracks raced */
	private List<Track> trackHistory;
	/** Time spent preparing on each race (milliseconds) */
	private List<Long> prepareTimes;
	/** Time spent on each race (milliseconds) */
	private List<Long> raceTimes;
	private Long timeStamp;
	
	/** 
	 * The current status. Status is PREPARING before each race, RACING during
	 * the race, and FINISHED after the final race.
	 */
	private Status status;
	
	/**
	 * Construct a tour from a setup
	 * @param setup
	 */
	public Tour(Setup setup) {
		this.setup = setup;
		raceNo = 0;
		maxRaces = 3;
		money = setup.getStartupMoney();
		purchasedCycles = new ArrayList<Cycle>();
		status = Status.PREPARING;
		raceSims = new ArrayList<RaceSim>();
		trackHistory = new ArrayList<Track>();
		prepareTimes = new ArrayList<Long>();
		raceTimes = new ArrayList<Long>();
		timeStamp = System.currentTimeMillis();
		System.out.println("Tour created. Starting money: $" + money);
	}
	
	/**
	 * Get a read-only list of cycles that can be purchased
	 * @return list of cycles that can be purchased
	 */
	public List<Cycle> getPurchasableCycles() {
		List<Cycle> purchasable = new ArrayList<Cycle>();
		for (Cycle c : setup.getCycles()) {
			if (c.getPrice() <= money) {
				purchasable.add(c);
			}
		}
		return purchasable;
	}
	
	/**
	 * Buy a cycle. Can only be done before any race.
	 * @param cycle The cycle to buy
	 * @return true iff success
	 */
	public boolean buyCycle(Cycle cycle) {
		if (raceNo != 0 || status != Status.PREPARING) {
			System.out.println("You can only buy cycles at the start of the tour");
			return false;
		} else if (cycle.getPrice() > money) {
			System.out.println("Insufficient money to buy this cycle");
			return false;
		} else if (!getPurchasableCycles().contains(cycle)) {
			System.out.println("Cycle is not available for purchase");
			return false;
		}
		purchasedCycles.add(cycle);
		money -= cycle.getPrice();
		System.out.println("Successfully purchased " + cycle.getName() + 
				". $" + money + " remaining");
		return true;
	}
	
	public List<Cycle> getPurchasedCycles() {
		return purchasedCycles;
	}
	
	public double getCurrentMoney() {
		return money;
	}
	
	/** 
	 * Returns list of tracks that can be chosen. A track can only be chosen if
	 * the current amount of money > the track's registration fee, and 
	 * the track has not been raced on before
	 * @return
	 */
	public List<Track> getAvailableTracks() {
		List<Track> result = new ArrayList<Track>();
		for (Track t : setup.getTracks()) {
			if (t.getRegistrationFee() <= money && !trackHistory.contains(t)) {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Start a race
	 * @param track The chosen track. Must be in getAvailableTracks()
	 * @param players The players, with the cycles and start positions set
	 * @return true iff race was started successfully
	 */
	public boolean startRace(Track track, List<Player> players) {
		if (status != Status.PREPARING) {
			System.out.println("Error: Not in PREPARING state.");
			return false;
		} else if (!getAvailableTracks().contains(track)) {
			System.out.println("Error: Track not available.");
			return false;
		} else if (players.isEmpty()) {
			System.out.println("Error: Need at least one player");
			return false;
		}

		@SuppressWarnings("unchecked")
		ArrayList<Cycle> temp = (ArrayList<Cycle>) purchasedCycles.clone();
		ArrayList<String> ids = new ArrayList<String>();
		for (Player p : players) {

			// Ensure that no two players with the same id exists.
			if (ids.contains(p.getId())) {
				System.out.println("Error: Multiple players with same id.");
				return false;
			} else {
				ids.add(p.getId());
			}
			
			// Ensure starting positions is set correctly w.r.t. id
			if (!p.getPosition().equals(
					track.getStartingPositions().get(p.getId()))) {
				System.out.println("Error: Invalid starting position or id.");
				return false;
			}
			
			// Ensure the cycle has been purchased
			if (!temp.contains(p.getCycle())) {
				System.out.println("Error: Cycle not available.");
				return false;
			} else {
				temp.remove(p.getCycle());
			}
			
			// damageCost must be set to 0
			if (p.getDamageCost() != 0) {
				System.out.println("Error: Damage cost must be 0");
				return false;
			}
			
			// Cannot start in obstacle mode
			if (p.isObstacle()) {
				System.out.println("Error: Cannot start in obstacle mode");
				return false;
			}
		}
		
		RaceState startState = new RaceState(players, track.getOpponents(),
				track.getDistractors());
		raceSims.add(new RaceSim(startState, track));
		raceNo++;
		trackHistory.add(track);
		status = Status.RACING;
		money -= track.getRegistrationFee() * players.size();
		System.out.println("Started race on track " + track.getFileNameNoPath()
				+ ". Current money: $" + money);
		
		// Calculate time taken to prepare
		prepareTimes.add(System.currentTimeMillis() - timeStamp);
		timeStamp = System.currentTimeMillis();
		
		return true;
	}
	
	public boolean isPreparing() {
		return status == Status.PREPARING;
	}
	
	public boolean isRacing() {
		return status == Status.RACING;
	}
	
	public boolean isFinished() {
		return status == Status.FINISHED;
	}
	
	/**
	 * Step a turn in the current race.
	 * @param actions List of actions. Size must match number of players.
	 */
	public void stepTurn(List<Action> actions) {
		if (!isRacing()) {
			System.out.println("Error: Not currently racing.");
			return;
		}
		RaceSim currentSim = raceSims.get(raceSims.size() - 1);
		currentSim.stepTurn(actions);
		if (currentSim.isFinished()) {
			if (raceNo == maxRaces) {
				status = Status.FINISHED;
			} else {
				status = Status.PREPARING;
			}
			money -= currentSim.getTotalDamageCost();
			if (currentSim.getCurrentStatus() == RaceState.Status.WON) {
				money += currentSim.getTrack().getPrize();
				System.out.println("Race finished. You won!");
				System.out.println("Damage costs: $" + currentSim.getTotalDamageCost()
						+ " Prize money: " + currentSim.getTrack().getPrize() 
						+ " Current money: " + money);
			} else {
				System.out.println("Race finished. You lost.");
				System.out.println(
						"Damage costs: $" + currentSim.getTotalDamageCost()
						+ " Current money: $" + money);
			}
			
			// Calculate time taken for race
			raceTimes.add(System.currentTimeMillis() - timeStamp);
			timeStamp = System.currentTimeMillis();			
		}
	}
	
	/**
	 * Return the latest race state. Returns null if no race state exists 
	 * in history.
	 * @return latest race state. 
	 */
	public RaceState getLatestRaceState() {
		if (raceSims.isEmpty()) {
			System.out.println("Error: no race in history.");
			return null;
		}
		return getCurrentSim().getCurrentState();
	}
	
	public Track getCurrentTrack() {
		if (!isRacing()) {
			System.out.println("Error: no race in progress.");
			return null;
		}
		return getCurrentSim().getTrack();
	}
	
	/**
	 * Returns the processing time in milliseconds taken to prepare for a race
	 * that has started or already ended.
	 * @param raceIndex Index of race
	 * @return race prepare time in milliseconds. Returns -1 if race does not
	 * exist
	 */
	public long getPrepareTime(int raceIndex) {
		if (prepareTimes.size() <= raceIndex) {
			System.out.println("Error: can't get time, race does not exist");
			return -1;
		} else {
			return prepareTimes.get(raceIndex);
		}
	}
	
	/**
	 * Returns the total processing time in milliseconds taken to run a race
	 * @param raceIndex Index of race
	 * @return race prepare time in milliseconds. Returns -1 if race does not
	 * exist
	 */
	public long getRaceTime(int raceIndex) {
		if (raceTimes.size() <= raceIndex) {
			System.out.println("Error: can't get time, race does not exist");
			return -1;
		} else {
			return raceTimes.get(raceIndex);
		}
	}
	
	/**
	 * Returns the maximum number of races allowed
	 * @return maximum number of races allowed
	 */
	public int getMaxRaces() {
		return maxRaces;
	}
	
	/**
	 * Returns the number of races completed or in progress
	 * @return the number of races completed or in progress
	 */
	public int getNumRaces() {
		return raceSims.size();
	}
	
	/**
	 * Returns the number of turns in a race
	 * @param raceIndex
	 * @return the number of turns in a race. -1 if race does not exist
	 */
	public int getTurnNo(int raceIndex) {
		if (raceSims.size() <= raceIndex) {
			System.out.println("Error: can't get no. of turns, race doesn't exist");
			return -1;			
		} else {
			return raceSims.get(raceIndex).getTurnNo();
		}
	}
	
	/**
	 * Outputs the tour data to file in the format described in the task sheet
	 * @param filename Name of file to save to
	 * @throws IOException 
	 */
	public void outputToFile(String filename) throws IOException {
		String ls = System.getProperty("line.separator");
		FileWriter output = new FileWriter(filename);
		output.write(setup.getCycleFileNoPath() + " " +
				setup.getMetaTrackFileNoPath() + ls);
		StringBuilder sb = new StringBuilder();
		for (Cycle c : purchasedCycles) {
			sb.append(c.getName() + " ");
		}
		output.write(sb.toString().trim() + ls);
		
		// Write races
		for (RaceSim sim : raceSims) {
			output.write(sim.getTrack().getFileNameNoPath() + ls);
			
			// Write each state and action
			for (int i = 0; i < sim.getStateHistory().size(); i++) {
				RaceState state = sim.getStateHistory().get(i);
				output.write(RaceSimTools.stateToString(state, sim.getTrack()));
				if (i < sim.getStateHistory().size() - 1) {
					ArrayList<Action> actions = sim.getActionHistory().get(i);
					List<Player> players = state.getPlayers();
					for (int j = 0; j < actions.size(); j++) {
						output.write(players.get(j).getId() + "-" +
								actions.get(j).toString() + " ");
					}
					output.write(ls);
				}
			}
			
			// Write money gained/lost from this race
			double moneyChange = sim.getTrack().getRegistrationFee() *
					sim.getCurrentState().getPlayers().size();
			if (sim.getCurrentStatus() == RaceState.Status.WON) {
				moneyChange += sim.getTrack().getPrize();
			}
			output.write(Double.toString(moneyChange));
			output.write(ls);
		}
		
		// Write money at end of tour
		output.write(Double.toString(money));
		output.close();
	}
	
	private RaceSim getCurrentSim() {
		return raceSims.get(raceSims.size() - 1);
	}

}
