
**Expense Manager App**

This section explains how code is modelled to achieve problem statement.

1) Group.java -> This class contains all group related stuff and its supporting functions.
2) User.java -> A class to handle users data.
3) Counter.java -> This class contains all incrementing counter like group expense, category expense, individual expense. Basic motive behind this class creation is to have one place to get all count related data.
4) IExpense.java -> This interface contains expense to percentage split or equal split. Kept both class at abstraction level as don't want to expose this to processor.
5) GroupManager.java -> This class manage all groups data and single place for CRUD operation on group.
6) GroupExpenseManager.java -> This class contain core business logic of handling the expense for all users. Getting expense summary and transitive settlement resides in this class.


Pros of this design:

1) Some business logic goes in domain for enforcing Domain driven design. Segregated business logic not required in processor.
2) Dynamic counters which reduces complexity on processor.
3) Can extend more functionality in EqualSplitExpense and PercentageSplitExpense.java without disturbing processor logic.

Cons of this design:

1) Summary could have been separated from processor.
2) Group domain class looks little on heavier side. Some logic could have been moved.


**Testing:** AppStartTest.java

Created 4 test for below scenarios.

1) All user equal share expense.
2) All user unequal share expense.
3) All user mix of equal and unequal expense.
4) Testing expense with more than 3 users.