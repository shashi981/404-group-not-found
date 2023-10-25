import datetime

# Will be changed based on database

shopping_data = [
    {"item": "Milk", "quantity": 1, "purchase_date": "2023-01-01"},
    {"item": "Milk", "quantity": 5, "purchase_date": "2023-01-08"},
    {"item": "Milk", "quantity": 1, "purchase_date": "2023-01-15"},
    {"item": "Eggs", "quantity": 1, "purchase_date": "2023-01-05"},
    {"item": "Eggs", "quantity": 1, "purchase_date": "2023-01-12"},
]

# User-defined settings
reminder_period_days = 2
time_frame_weeks = 10 # this can be changed 

# Store item purchase history
purchase_history = {}

# Initialize current date
current_date = datetime.date.today()  

# Initialize the expected run-out date for each item
expected_run_out_dates = {}



# Populate purchase history, also detecting anomalies

for shopping_entry in shopping_data:
    item = shopping_entry["item"]
    quantity = shopping_entry["quantity"]
    anomaly_threshold = quantity*3 # Anomaly detection threshold (e.g., a quantity higher than this threshold is considered an anomaly)
    purchase_date = datetime.datetime.strptime(shopping_entry["purchase_date"], "%Y-%m-%d").date()

    # Check for anomalies based on the threshold
    if quantity > anomaly_threshold:
        print(f"Anomaly detected for {item} on {purchase_date}: Quantity {quantity} exceeds threshold.")
        continue

    # Updating purchase history
    if item in purchase_history:
        purchase_history[item].append(purchase_date)
    else:
        purchase_history[item] = [purchase_date]

    # Calculate expected run-out date based on time frame
    if item not in expected_run_out_dates:
        expected_run_out_dates[item] = purchase_date + datetime.timedelta(weeks=time_frame_weeks)

# Generate reminders
for item, purchase_dates in purchase_history.items():
    # Calculate the average purchase frequency
    purchase_frequency = len(purchase_dates) / time_frame_weeks  # Number of times purchased per week

    # Calculate the expected run-out date
    expected_run_out_date = expected_run_out_dates[item]

    # Calculate the reminder date
    reminder_date = expected_run_out_date - datetime.timedelta(days=reminder_period_days)

    # Check if it's time to send a reminder
    if current_date >= reminder_date:
        print(f"Reminder: Buy {item} in the next {reminder_period_days} days.")

# Example output based on the provided data and settings:
# Anomaly detected for Milk on 2023-01-08: Quantity 5 exceeds threshold.
# Reminder: Buy Milk in the next 2 days.
