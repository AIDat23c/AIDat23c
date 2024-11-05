document.addEventListener("DOMContentLoaded", function() {
    fetch('https://api.the-odds-api.com/v4/sports?apiKey=0fccdfb8828cb3428fff6d71f103289f')
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
        const url = `https://api.the-odds-api.com/v4/sports/${selectedLeague}/odds/?regions=eu&markets=h2h&apiKey=0fccdfb8828cb3428fff6d71f103289f`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                // Clear previous options
                bookmakersSelect.innerHTML = '<option value="">Select a Bookmaker</option>';

                // Set to track added bookmakers
                const addedBookmakers = new Set();

                // Extract unique bookmakers
                data.forEach(match => {
                    match.bookmakers.forEach(bookmaker => {
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
                });
            })
            .catch(error => console.error('Error fetching bookmakers:', error));
    } else {
        // Clear the bookmaker select if no league is selected
        bookmakersSelect.innerHTML = '<option value="">Select a Bookmaker</option>';
    }
});
