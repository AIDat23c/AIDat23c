const backendURL = "http://localhost:8080/api/openai";

// Load leagues when the DOM is fully loaded
document.addEventListener("DOMContentLoaded", function () {
    fetchLeagues();
});

function fetchLeagues() {
    fetch(`${backendURL}/leagues`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const leagueSelect = document.getElementById('leagueSelect');
            // Filter leagues that start with 'soccer' and populate dropdown
            const soccerLeagues = data.filter(league => league.key.startsWith('soccer'));
            soccerLeagues.forEach(league => {
                const option = document.createElement('option');
                option.value = league.key;
                option.textContent = league.title;
                leagueSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching leagues:', error));
}

// Event listener for fetching bookmakers based on selected league
document.getElementById('leagueSelect').addEventListener('change', function () {
    const selectedLeague = this.value;
    fetchBookmakers(selectedLeague);
});

function fetchBookmakers(leagueId) {
    const bookmakersSelect = document.getElementById('bookmakerSelect');
    bookmakersSelect.innerHTML = '<option value="">Select a Bookmaker</option>'; // Reset options

    if (leagueId) {
        fetch(`${backendURL}/bookmakers/${leagueId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
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
    }
}

// Submit button event listener for generating betting advice
document.getElementById("button_send").addEventListener("click", function (event) {
    event.preventDefault(); // Prevent form submission
    submitBetRequest(event.target); // Pass button as parameter
});

function submitBetRequest(button) {
    // Disable the button to prevent multiple submissions
    button.disabled = true;

    // Gather form values
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

    if(userInput.length > 600) {
        alert("Your custom request is too long.")
        button.disabled = false;
        return;
    }

    document.getElementById("loading-wheel").style.display = "block";

    // Create the request body object
    const requestBody = {
        amountOfMatches: parseInt(amountOfMatches, 10),
        moneyReturned: parseInt(moneyReturned, 10),
        league: league,
        bookmaker: bookmaker,
        userInput: userInput
    };

    // Send POST request to the backend
    fetch(`${backendURL}/generate`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(data => {
            displayResult(data.answer);
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred while processing your request. Too many requests. Please try again later.");
        })
        .finally(() => {
            // Re-enable the button after the response is received
            document.getElementById("loading-wheel").style.display = "none";
            button.disabled = false;
        });
}

function displayResult(answer) {
    const responseDiv = document.getElementById('response');
    if (responseDiv) {
        responseDiv.textContent = answer;
    } else {
        alert('Result: ' + answer);
    }
}
