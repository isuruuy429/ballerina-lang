import { Assignment, ASTNode, ASTUtil, Block, ExpressionStatement, Function as BalFunction,
    Invocation, Return, Service, VariableDef, Visitor, WorkerSend } from "@ballerina/ast-model";
import * as _ from "lodash";
import { FunctionViewState, StmntViewState, ViewState } from "../../view-model/index";

const currentState: {
    topLevelNode?: ASTNode | undefined,
    blocks: ASTNode[],
    currentBlock?: ASTNode,
    statement?: ASTNode | undefined,
} = {
    blocks: [],
};

export const visitor: Visitor = {
    beginVisitASTNode(node: ASTNode) {
        if (node.viewState) {
            // by default hide all elements
            node.viewState.hidden = true;
        }
    },

    endVisitService(node: Service) {
        node.viewState.hidden = false;
    },

    endVisitFunction(node: BalFunction) {
        const viewState: FunctionViewState = node.viewState;
        if (!viewState.isExpandedFunction) {
            viewState.hidden = false;
        }
    },

    beginVisitBlock(node: Block) {
        if (!node.parent) {
            return;
        }
        // if (node.parent.viewState.hidden !== false) {
        //     // if its already set to false that means its already checked for visibility
        //     node.parent.viewState.hidden = true;
        // }
        currentState.blocks.push(node.parent);
        currentState.currentBlock = node.parent;
    },

    endVisitBlock(node: Block) {
        const visitedNode = currentState.blocks.pop();
        const { blocks } = currentState;
        currentState.currentBlock = blocks.length > 0 ? blocks[blocks.length - 1] : undefined;

        // If a child block is visible then the parent block should also be visible
        if (visitedNode && !visitedNode.viewState.hidden && currentState.currentBlock) {
            currentState.currentBlock.viewState.hidden = false;
        }
    },

    beginVisitVariableDef(node: VariableDef) {
        node.viewState.hidden = true;
        currentState.statement = node;
    },

    endVisitVariableDef(node: VariableDef) {
        currentState.statement = undefined;
    },

    beginVisitExpressionStatement(node: ExpressionStatement) {
        node.viewState.hidden = true;
        currentState.statement = node;
    },

    endVisitExpressionStatement(node: ExpressionStatement) {
        currentState.statement = undefined;
    },

    beginVisitAssignment(node: Assignment) {
        node.viewState.hidden = true;
        currentState.statement = node;
    },

    endVisitAssignment(node: Assignment) {
        currentState.statement = undefined;
    },

    beginVisitReturn(node: Return) {
        currentState.statement = node;
        node.viewState.hidden = false;
    },

    endVisitReturn(node: Return) {
        currentState.statement = undefined;
    },

    beginVisitWaitExpr() {
        if (currentState.statement) {
            currentState.statement.viewState.hidden = false;
        }
    },

    beginVisitWorkerSend(node: WorkerSend) {
        if (currentState.statement) {
            currentState.statement.viewState.hidden = false;
        }
        const statement = currentState.statement ? currentState.statement : node;
        (statement.viewState as ViewState).hidden = false;
    },

    beginVisitWorkerSyncSend(node: WorkerSend) {
        if (currentState.statement) {
            currentState.statement.viewState.hidden = false;
        }
        const statement = currentState.statement ? currentState.statement : node;
        (statement.viewState as ViewState).hidden = false;
    },

    beginVisitWorkerReceive(node: WorkerSend) {
        if (currentState.statement) {
            currentState.statement.viewState.hidden = false;
        }
        const statement = currentState.statement ? currentState.statement : node;
        (statement.viewState as ViewState).hidden = false;
    },

    beginVisitInvocation(node: Invocation) {
        const currentStatement = currentState.statement;
        const { currentBlock } = currentState;
        if (node.actionInvocation) {
            if (currentStatement) {
                currentStatement.viewState.hidden = false;
            }
            if (currentBlock) {
                currentBlock.viewState.hidden = false;
            }
            return;
        }

        if (currentStatement) {
            const statementViewState: StmntViewState = currentStatement.viewState;
            if (!(statementViewState.expandContext && statementViewState.expandContext.expandedSubTree)) {
                return;
            }
            const { expandedSubTree } = statementViewState.expandContext;
            ASTUtil.traversNode(expandedSubTree, visitor);
            if (!expandedSubTree.viewState.hidden) {
                currentStatement.viewState.hidden = false;
                if (currentBlock) {
                    currentBlock.viewState.hidden = false;
                }
            }
        }
    },
};
