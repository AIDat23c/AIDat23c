const backendURL = "http://localhost:8080/api/openai";

// Global variables to store leagues and bookmakers
let soccerLeagues = [];
const bookmakersPerLeague = {};

document.addEventListener("DOMContentLoaded", function () {
    fetch(backendURL + '/leagues')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            soccerLeagues = data.filter(sport => sport.key.startsWith('soccer'));
            const leagueSelect = document.getElementById('leagueSelect');

            soccerLeagues.forEach(league => {
                const option = document.createElement('option');
                option.value = league.key;
                option.textContent = league.title;
                leagueSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching leagues:', error));
});

// Event listener for league selection to fetch bookmakers
document.getElementById('leagueSelect').addEventListener('change', function () {
    const selectedLeague = this.value;
    const bookmakersSelect = document.getElementById('bookmakerSelect');

    if (selectedLeague) {
        const url = `${backendURL}/bookmakers/${selectedLeague}`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                bookmakersSelect.innerHTML = '<option value="">Select a Bookmaker</option>';

                const addedBookmakers = new Set();
                data.forEach(bookmaker => {
                    if (!addedBookmakers.has(bookmaker.key)) {
                        const option = document.createElement('option');
                        option.value = bookmaker.key;
                        option.textContent = bookmaker.title;
                        bookmakersSelect.appendChild(option);
                        addedBookmakers.add(bookmaker.key);
                    }
                });
            })
            .catch(error => console.error('Error fetching bookmakers:', error));
    } else {
        bookmakersSelect.innerHTML = '<option value="">Select a Bookmaker</option>';
    }
});

// Event listener for generating a specific bet
document.getElementById("button_send").addEventListener("click", function (event) {
    event.preventDefault();

    const amountOfMatches = document.getElementById("matches").value;
    const moneyReturned = document.getElementById("return").value;
    const league = document.getElementById("leagueSelect").value;
    const bookmaker = document.getElementById("bookmakerSelect").value;
    const userInput = document.getElementById("userInput").value;

    // Validate input fields
    if (!amountOfMatches || !moneyReturned || !league || !bookmaker) {
        alert("Please fill in all required fields.");
        button.disabled = false;
        return;
    }

    if (isNaN(amountOfMatches) || amountOfMatches <= 0 || amountOfMatches > 10) {
        alert("Please enter a valid number for Amount of Matches.");
        button.disabled = false;
        return;
    }

    if (isNaN(moneyReturned) || moneyReturned <= 0 || moneyReturned > 1000) {
        alert("Please enter a valid number for Money Returned.");
        button.disabled = false;
        return;
    }

    if (userInput.length > 600) {
        alert("Your custom request is too long.")
        button.disabled = false;
        return;
    }

    document.getElementById("loading-wheel").style.display = "block";
    const requestBody = {
        amountOfMatches: parseInt(amountOfMatches, 10),
        moneyReturned: parseInt(moneyReturned, 10),
        league: league,
        bookmaker: bookmaker,
        userInput: userInput
    };

    fetch(`${backendURL}/generate`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text)
                });
            }
            return response.json();
        })
        .then(data => {
            const responseDiv = document.getElementById('response');
            if (responseDiv) {
                responseDiv.style.whiteSpace = 'pre-wrap';
                responseDiv.textContent = data.answer || "No response message";
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred while processing your request. Please try again later.");
        }).finally(() => {
        // Re-enable the button after the response is received
        document.getElementById("loading-wheel").style.display = "none";
        button.disabled = false;
    });
});

// Event listener for generating a random bet
document.getElementById("button_random2").addEventListener("click", function () {
    const apiEndpoint = "/generate-random";
    const responseDiv = document.getElementById("response");
    const button = document.getElementById("button_random2");

    button.disabled = true;
    button.textContent = "Generating...";

    fetch(backendURL + apiEndpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to generate a random bet");
            }
            return response.json();
        })
        .then(data => {
            if (responseDiv) {
                responseDiv.style.whiteSpace = 'pre-wrap';
                responseDiv.textContent = data.answer || "No response message";
            }
        })
        .catch(error => {
            console.error("Error:", error);
            if (responseDiv) {
                responseDiv.textContent = "An error occurred while generating the bet.";
            }
        })
        .finally(() => {
            button.disabled = false;
            button.textContent = "Generate Random Bet";
        });
});
