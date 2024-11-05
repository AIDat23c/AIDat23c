const backendURL = "http://localhost:8080/api/openai";

document.addEventListener("DOMContentLoaded", function() {
    fetch(backendURL + '/leagues')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const soccerLeagues = data.filter(sport => sport.key.startsWith('soccer'));
            const leagueSelect = document.getElementById('leagueSelect');

            soccerLeagues.forEach(league => {
                const option = document.createElement('option');
                option.value = league.key; // Use the league key as the value
                option.textContent = league.title; // Display the league title
                leagueSelect.appendChild(option); // Add the option to the select
            });
        })
        .catch(error => console.error('Error fetching leagues:', error));
});

// Event listener for league selection to fetch bookmakers
document.getElementById('leagueSelect').addEventListener('change', function() {
    const selectedLeague = this.value;
    const bookmakersSelect = document.getElementById('bookmakerSelect');

    if (selectedLeague) {
        const url = `${backendURL}/bookmakers/${selectedLeague}`;

        console.log('Fetching URL:', url); // Debugging URL

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                console.log('Data received:', data); // Debugging data

                // Clear previous options
                bookmakersSelect.innerHTML = '<option value="">Select a Bookmaker</option>';

                // Set to track added bookmakers
                const addedBookmakers = new Set();

                // Since data is an array of bookmakers, iterate over it directly
                data.forEach(bookmaker => {
                    // Check if the bookmaker is already added
                    if (!addedBookmakers.has(bookmaker.key)) {
                        const option = document.createElement('option');
                        option.value = bookmaker.key; // Use the bookmaker key as the value
                        option.textContent = bookmaker.title; // Display the bookmaker title
                        bookmakersSelect.appendChild(option); // Add the option to the select

                        // Add the bookmaker to the set
                        addedBookmakers.add(bookmaker.key);
                    }
                });
            })
            .catch(error => console.error('Error fetching bookmakers:', error));
    } else {
        // Clear the bookmaker select if no league is selected
        bookmakersSelect.innerHTML = '<option value="">Select a Bookmaker</option>';
    }
});

document.getElementById("button_send").addEventListener("click", function (event) {
    event.preventDefault(); // Prevent form from submitting

    // Get the values of the request parameters
    const amountOfMatches = document.getElementById("matches").value;
    const moneyReturned = document.getElementById("return").value;
    const league = document.getElementById("leagueSelect").value;
    const bookmaker = document.getElementById("bookmakerSelect").value;

    // Validate inputs
    if (!amountOfMatches || !moneyReturned || !league || !bookmaker) {
        alert("Please fill in all required fields.");
        return;
    }

    if (isNaN(amountOfMatches) || amountOfMatches <= 0) {
        alert("Please enter a valid number for Amount of Matches.");
        return;
    }

    if (isNaN(moneyReturned) || moneyReturned <= 0) {
        alert("Please enter a valid number for Money Returned.");
        return;
    }

    // Create the request body object
    const requestBody = {
        amountOfMatches: parseInt(amountOfMatches, 10),
        moneyReturned: parseInt(moneyReturned, 10),
        league: league,
        bookmaker: bookmaker
    };

    // Send the POST request to the backend
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
            // Handle the response data here
            console.log('Response from backend:', data);

            // Display the response message in the 'response' div
            const responseDiv = document.getElementById('response');
            if (responseDiv) {
                responseDiv.textContent = data.answer;
            } else {
                alert('Result: ' + data.answer);
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred while processing your request. Please try again later.");
        });
});






