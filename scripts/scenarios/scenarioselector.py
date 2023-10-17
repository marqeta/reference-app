import sys
import os
print("Purpose of this script is to simulate transaction flows you could potentially encounter.")
# For more information about transactions: https://www.marqeta.com/docs/developer-guides/about-transactions
print("Choose a scenario from the following. Press any other key to exit")
print("1. Basic authorization and clearing transaction flow")
print("2. Authorization and clearing transaction flow with multiple clearings for a single authorization")
print("3. Authorization and clearing transaction flow with clearing amount greater than authorization amount")
print("4. Clearing only")
print("5. Authorization --> advice -- > clearing linked to advice ")
print("6. Authorization --> advice -- > clearing linked to authorization")
print("7. Authorization --> incremental --> clearing flow")
print("8. Authorization with multiple incremental transactions  followed by clearing")
print("9. Error - Simulate a delayed funding response")
print("10. Declining a funding authorization request")
print("11. Basic PIN debit scenario")
print("12. Authorization --> reversal")
print("13. Authorization -->  partial clearing --> reversal")
print("14. Authorization --> complete clearing --> refund")


while 1:
    choice=input(f"Enter scenario number to execute:")
    if choice == "1":
        exec(open(os.path.join(sys.path[0], "authandclear.py")).read())
    elif choice == "2":
        exec(open(os.path.join(sys.path[0],"authandpartialclear.py")).read())
    elif choice == "3":
        exec(open(os.path.join(sys.path[0],"authandoverclear.py")).read())
    elif choice == "4":
        exec(open(os.path.join(sys.path[0], "clearforcepost.py")).read())
    elif choice == "5":
        exec(open(os.path.join(sys.path[0],"authadviceclear.py")).read())
    elif choice == "6":
        exec(open(os.path.join(sys.path[0],"authadviceclear2.py")).read())
    elif choice == "7":
        exec(open(os.path.join(sys.path[0],"authincrementalclear.py")).read())
    elif choice == "8":
        exec(open(os.path.join(sys.path[0],"authmultiincrementalclear.py")).read())
    elif choice == "9":
        exec(open(os.path.join(sys.path[0],"authdelay.py")).read())
    elif choice == "10":
        exec(open(os.path.join(sys.path[0],"authdecline.py")).read())
    elif choice == "11":
        exec(open(os.path.join(sys.path[0], "singlepindebit.py")).read())
    elif choice == "12":
        exec(open(os.path.join(sys.path[0],"authandreversal.py")).read())
    elif choice == "13":
        exec(open(os.path.join(sys.path[0],"authclearreversal.py")).read())
    elif choice == "14":
        exec(open(os.path.join(sys.path[0],"authclearrefund.py")).read())
    else:
        sys.exit(1)
