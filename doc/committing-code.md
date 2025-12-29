Instructions for committing code to the project

# High level rules

1. Follow the Commit Messages rules
3. For applicable tasks, you should aim to use Atomic Commits
2. All commits should be done via a Pull Request.


## Commit Messages

1. Limit the subject line to 50 characters
2. Capitalize the subject line
3. Include the issue number in the commit. Example: "(#1234)"

Further reading: https://cbea.ms/git-commit/


## Atomic Commits

Please read this excellent blog post on Atomic Commits: https://www.freshconsulting.com/insights/blog/atomic-commits/

> ## Atomic Approach
>
> * Commit each fix or task as a separate change
> * Only commit when a block of work is complete
> * Commit each layout change separately
> * Joint commit for layout file, code behind file, and additional resources
>
> ## Benefits
>
> * Easy to roll back without affecting other changes
> * Easy to make other changes on the fly
> * Easy to merge features to other branches

Please note the use of Atomic Commits is NOT just about splitting changes into multiple commits. 
The changes introduced by each commit should be complete in itself. The application should be in a working state before and after the commit. 
As an example, DO NOT change an `interface` in one commit, then update the implementation in a different commit. This leaves compilation broken between the two commits, which we do not want.

### Example

Each of the following should be their own commit:
1. **Define Model/Data Classes**: Commit the initial data structures or models that will be used throughout the feature or fix.
2. **Define `interface`**: If applicable, define interfaces or abstract classes that establish a contract for implementations.
3. **Add Implementation(s) of Interface**: Implement the previously defined interfaces with concrete logic.
4. **Update DI for New Class**: Update the Dependency Injection (DI) configuration to include the new implementations.
5. **Use New Class**: Integrate the new class or feature into the existing codebase, making sure it interacts correctly with other components.


## Pull Requests

All tickets should be done with via a Github Pull Request.

Steps to make a pull request:
1. Before making the PR, you should `git merge main` to ensure the latest code is merged, and deal with any merge conflicts.
2. Check to ensure your PR only contains necessary changes.
3. Check the formatting of your changes.

