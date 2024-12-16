import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TSPMultipopulationGA {

    // Parâmetros do problema
    private static final int NUM_CITIES = 10; // Número de cidades
    private static final int[][] distances = { // Matriz de distâncias fornecida
            {0, 106, 30, 138, 190, 0, 175, 89, 56, 0},
            {106, 0, 0, 0, 265, 105, 110, 0, 0, 160},
            {30, 0, 0, 117, 221, 0, 205, 66, 47, 0},
            {138, 0, 117, 0, 0, 265, 0, 163, 0, 186},
            {190, 265, 221, 0, 0, 0, 137, 114, 0, 0},
            {0, 105, 0, 265, 0, 0, 185, 0, 0, 75},
            {175, 110, 205, 0, 137, 185, 0, 145, 0, 0},
            {89, 0, 66, 163, 114, 0, 145, 0, 111, 0},
            {56, 0, 47, 0, 0, 0, 0, 111, 0, 0},
            {0, 160, 0, 186, 0, 75, 0, 0, 0, 0}
    };

    // Parâmetros do AG
    private static final int POPULATION_SIZE = 50; // Tamanho da população
    private static final int NUM_GENERATIONS = 1000; // Número máximo de gerações
    private static final double MUTATION_RATE = 0.02; // Taxa de mutação
    private static final int NUM_POPULATIONS = 3; // Número de populações
    private static final Random random = new Random();

    public static void main(String[] args) {
        ArrayList<ArrayList<ArrayList<Integer>>> populations = new ArrayList<>();

        // Inicializa múltiplas populações
        for (int i = 0; i < NUM_POPULATIONS; i++) {
            populations.add(initializePopulation());
        }

        // Evolução das populações
        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            for (int i = 0; i < NUM_POPULATIONS; i++) {
                populations.set(i, evolvePopulation(populations.get(i)));
            }

            // Realiza migração entre populações a cada 100 gerações
            if (generation % 100 == 0) {
                migrate(populations);
            }
        }

        // Avalia as melhores soluções
        ArrayList<Integer> bestSolution = null;
        int bestFitness = Integer.MAX_VALUE;

        for (ArrayList<ArrayList<Integer>> population : populations) {
            for (ArrayList<Integer> individual : population) {
                int fitness = calculateFitness(individual);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestSolution = individual;
                }
            }
        }

        System.out.println("Melhor solução encontrada: " + bestSolution);
        System.out.println("Distância total: " + bestFitness);
    }

    // Inicializa uma população aleatória
    private static ArrayList<ArrayList<Integer>> initializePopulation() {
        ArrayList<ArrayList<Integer>> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            ArrayList<Integer> individual = new ArrayList<>();
            for (int j = 0; j < NUM_CITIES; j++) {
                individual.add(j);
            }
            Collections.shuffle(individual);
            population.add(individual);
        }
        return population;
    }

    // Evolui uma população
    private static ArrayList<ArrayList<Integer>> evolvePopulation(ArrayList<ArrayList<Integer>> population) {
        ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<>();

        // Elitismo: mantém o melhor indivíduo
        population.sort((a, b) -> calculateFitness(a) - calculateFitness(b));
        newPopulation.add(new ArrayList<>(population.get(0)));

        // Realiza crossover e mutação
        while (newPopulation.size() < POPULATION_SIZE) {
            ArrayList<Integer> parent1 = tournamentSelection(population);
            ArrayList<Integer> parent2 = tournamentSelection(population);
            ArrayList<Integer> offspring = crossover(parent1, parent2);
            mutate(offspring);
            newPopulation.add(offspring);
        }

        return newPopulation;
    }

    // Torneio de seleção
    private static ArrayList<Integer> tournamentSelection(ArrayList<ArrayList<Integer>> population) {
        ArrayList<Integer> best = population.get(random.nextInt(population.size()));
        for (int i = 1; i < 3; i++) {
            ArrayList<Integer> contender = population.get(random.nextInt(population.size()));
            if (calculateFitness(contender) < calculateFitness(best)) {
                best = contender;
            }
        }
        return best;
    }

    // Crossover (OX - Order Crossover)
    private static ArrayList<Integer> crossover(ArrayList<Integer> parent1, ArrayList<Integer> parent2) {
        int start = random.nextInt(NUM_CITIES);
        int end = random.nextInt(NUM_CITIES - start) + start;

        ArrayList<Integer> child = new ArrayList<>(Collections.nCopies(NUM_CITIES, -1));
        for (int i = start; i <= end; i++) {
            child.set(i, parent1.get(i));
        }

        int currentIndex = (end + 1) % NUM_CITIES;
        for (int i = 0; i < NUM_CITIES; i++) {
            int city = parent2.get((end + 1 + i) % NUM_CITIES);
            if (!child.contains(city)) {
                child.set(currentIndex, city);
                currentIndex = (currentIndex + 1) % NUM_CITIES;
            }
        }
        return child;
    }

    // Mutação (swap de dois genes)
    private static void mutate(ArrayList<Integer> individual) {
        if (random.nextDouble() < MUTATION_RATE) {
            int i = random.nextInt(NUM_CITIES);
            int j = random.nextInt(NUM_CITIES);
            Collections.swap(individual, i, j);
        }
    }

    // Calcula o custo (fitness) de uma solução
    private static int calculateFitness(ArrayList<Integer> individual) {
        int totalDistance = 0;
        for (int i = 0; i < NUM_CITIES - 1; i++) {
            totalDistance += distances[individual.get(i)][individual.get(i + 1)];
        }
        totalDistance += distances[individual.get(NUM_CITIES - 1)][individual.get(0)];
        return totalDistance;
    }

    // Migração entre populações
    private static void migrate(ArrayList<ArrayList<ArrayList<Integer>>> populations) {
        for (int i = 0; i < populations.size(); i++) {
            int target = (i + 1) % populations.size();
            ArrayList<Integer> migrant = populations.get(i).remove(0);
            populations.get(target).add(migrant);
        }
    }
}


